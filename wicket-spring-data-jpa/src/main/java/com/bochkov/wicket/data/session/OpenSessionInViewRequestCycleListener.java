package com.bochkov.wicket.data.session;

import lombok.extern.log4j.Log4j;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


public class OpenSessionInViewRequestCycleListener implements IRequestCycleListener {
    public static final String DEFAULT_SESSION_FACTORY_BEAN_NAME = "sessionFactory";
    private static final MetaDataKey<Boolean> PARTICIPATE = new MetaDataKey<Boolean>() {
    };
    private static final MetaDataKey<SessionFactory> SESSION_FACTORY = new MetaDataKey<SessionFactory>() {
    };
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final WebApplication application;
    private String sessionFactoryBeanName = DEFAULT_SESSION_FACTORY_BEAN_NAME;

    public OpenSessionInViewRequestCycleListener(WebApplication application) {
        this.application = application;
    }

    /**
     * Return the bean name of the SessionFactory to fetch from Spring's
     * root application context.
     */
    protected String getSessionFactoryBeanName() {
        return this.sessionFactoryBeanName;
    }

    /**
     * Set the bean name of the SessionFactory to fetch from Spring's
     * root application context. Default is "sessionFactory".
     *
     * @see #DEFAULT_SESSION_FACTORY_BEAN_NAME
     */
    public void setSessionFactoryBeanName(String sessionFactoryBeanName) {
        this.sessionFactoryBeanName = sessionFactoryBeanName;
    }

    @Override
    public void onBeginRequest(RequestCycle cycle) {
        SessionFactory sessionFactory = lookupSessionFactory(cycle);
        cycle.setMetaData(SESSION_FACTORY, sessionFactory);
        cycle.setMetaData(PARTICIPATE, false);

        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // Do not modify the Session: just set the participate flag.
            cycle.setMetaData(PARTICIPATE, true);
        } else {
            logger.debug("Opening Hibernate Session in OpenSessionInViewRequestCycleListener");
            Session session = openSession(sessionFactory);
            SessionHolder sessionHolder = new SessionHolder(session);
            TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
        }
    }

    @Override
    public void onEndRequest(RequestCycle cycle) {
        if (!cycle.getMetaData(PARTICIPATE)) {
            SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(cycle.getMetaData(SESSION_FACTORY));
            logger.debug("Closing Hibernate Session in OpenSessionInViewFilter");
            SessionFactoryUtils.closeSession(sessionHolder.getSession());
        }
    }

    /**
     * Look up the SessionFactory that this filter should use,
     * taking the current HTTP request as argument.
     * <p>The default implementation delegates to the {@link #lookupSessionFactory()}
     * variant without arguments.
     *
     * @param cycle the current request
     * @return the SessionFactory to use
     */
    protected SessionFactory lookupSessionFactory(RequestCycle cycle) {
        return lookupSessionFactory();
    }

    /**
     * Look up the SessionFactory that this filter should use.
     * <p>The default implementation looks for a bean with the specified name
     * in Spring's root application context.
     *
     * @return the SessionFactory to use
     * @see #getSessionFactoryBeanName
     */
    protected SessionFactory lookupSessionFactory() {
        if (logger.isDebugEnabled()) {
            logger.debug("Using SessionFactory '" + getSessionFactoryBeanName() + "' for OpenSessionInViewFilter");
        }
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(this.application.getServletContext());
        return wac.getBean(getSessionFactoryBeanName(), SessionFactory.class);
    }

    /**
     * Open a Session for the SessionFactory that this filter uses.
     * <p>The default implementation delegates to the
     * {@code SessionFactory.openSession} method and
     * sets the {@code Session}'s flush mode to "MANUAL".
     *
     * @param sessionFactory the SessionFactory that this filter uses
     * @return the Session to use
     * @throws DataAccessResourceFailureException if the Session could not be created
     * @see org.hibernate.FlushMode#MANUAL
     */
    protected Session openSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
        try {
            Session session = sessionFactory.openSession();
            session.setHibernateFlushMode(FlushMode.MANUAL);
            return session;
        } catch (HibernateException ex) {
            throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
        }
    }

}
