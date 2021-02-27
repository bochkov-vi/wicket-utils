package com.bochkov.wicket.jpa.crud;

import com.bochkov.bootstrap.FormComponentErrorBehavior;
import com.bochkov.wicket.jpa.crud.duplicate.DuplicateError;
import com.bochkov.wicket.jpa.model.PersistableModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.springframework.core.NestedRuntimeException;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Accessors(chain = true)
public abstract class CrudPanel<T, ENTITY extends Persistable<ID>, ID extends Serializable> extends GenericPanel<T> {

    public static MetaDataKey<Page> BACKPAGE = new MetaDataKey<Page>() {
    };

    @Getter
    @Setter
    protected boolean ajax = false;

    protected org.slf4j.Logger log;

    protected FeedbackPanel feedback = new FeedbackPanel("feedback", new IFeedbackMessageFilter() {
        @Override
        public boolean accept(FeedbackMessage message) {
            return !message.isRendered() && !(message.getMessage() instanceof DuplicateError) && !FormComponentErrorBehavior.canRender(message);
        }
    });

    protected DeletePanel<ENTITY, ID> deletePanel = new DeletePanel<ENTITY, ID>("deleted-panel") {
        @Override
        public void onDelete(AjaxRequestTarget target, IModel model) {
            CrudPanel.this.onDelete(target, model);
        }
    };

    protected Class<ENTITY> entityClass;

    @Getter
    @Setter
    Consumer<Optional<AjaxRequestTarget>> onBack = new SerializableConsumer<Optional<AjaxRequestTarget>>() {
        @Override
        public void accept(Optional<AjaxRequestTarget> ajaxRequestTarget) {
            if (getBackPage() != null) {
                setResponsePage(getBackPage());
            }
        }
    };

    public CrudPanel(String id, Class<ENTITY> entityClass) {
        super(id);
        this.entityClass = entityClass;
        log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    }

    public CrudPanel(String id, Class<ENTITY> entityClass, IModel<T> model) {
        super(id, model);
        this.entityClass = entityClass;
        log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    }

    public static Page setBackPageMeta(Page backPage, Page targetPage) {
        targetPage.setMetaData(BACKPAGE, backPage);
        return targetPage;
    }

    public static Page getBackPageMeta(Page targetPage) {
        return targetPage.getMetaData(BACKPAGE);
    }

    public Page getBackPage() {
        return getBackPageMeta(getPage());
    }

    public CrudPanel<T, ENTITY, ID> setBackPage(Page backPage) {
        setBackPageMeta(backPage, getPage());
        return this;
    }

    protected abstract <R extends CrudRepository<ENTITY, ID>> R getRepository();

    public void onDelete(AjaxRequestTarget target, IModel<ENTITY> model) {

        if (model != null) {
            target.add(feedback);
            ENTITY entity = model.getObject();
            if (entity != null && !entity.isNew()) {
                try {
                    getRepository().delete(model.getObject());
                    deletePanel.hide(target);
                    info(new StringResourceModel("delete.success", this, model).setParameters(model.getObject()).getObject());
                    onAfterDelete(target);
                } catch (Exception e) {
                    String message = new StringResourceModel("delete.error", this, model).setParameters(model.getObject()).getObject();
                    error(((NestedRuntimeException) e).getMostSpecificCause());
                    log.error(message, e);
                }
            } else {
                error(new StringResourceModel("delete.empty.error", this, model).setParameters(model.getObject()).getObject());
            }
        }
    }

    public void onAfterDelete(AjaxRequestTarget target) {

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        deletePanel.setDeletedEntityModel(PersistableModel.of(id -> getRepository().findById(id)));
        deletePanel.add(createDetails("details", deletePanel.deletedEntityModel));
        deletePanel.setDefaultModel(Model.of());
        add(feedback);
        feedback.setOutputMarkupId(true);
        add(deletePanel);
    }

    public AbstractLink createBackButton(String id, boolean ajax) {
        AbstractLink button = null;
        if (ajax) {
            button = createAjaxBackButton(id);
        } else {
            button = createSimpleBackButton(id);
        }
        button.setEscapeModelStrings(false);
        button.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("btn");
                oldClasses.add("btn-outline-info");
                return oldClasses;
            }
        });
        button.setBody(Model.of("<span class='fa fa-mail-reply'></span>"));
        return button;
    }

    private AbstractLink createAjaxBackButton(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onBack(Optional.of(target));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisible(getBackPage() != null);
            }
        };
    }

    private AbstractLink createSimpleBackButton(String id) {
        return new Link<Void>(id) {

            @Override
            public void onClick() {
                onBack(Optional.empty());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisible(getBackPage() != null);
            }
        };
    }

    public void onBack(Optional<AjaxRequestTarget> target) {
        this.onBack.accept(target);
    }

    public void onRequestDelete(AjaxRequestTarget target, IModel<ENTITY> model) {
        deletePanel.deletedEntityModel.setObject(model.getObject());
        deletePanel.setDefaultModelObject(getString("deleteDialogHeader"));
        deletePanel.show(target);
    }

    public final Component createDeleteButton(String id, IModel model) {
        AbstractLink button = null;

        button = createDeleteAjaxButton(id, model);

        button.setEscapeModelStrings(false);
        button.setBody(Model.of("<span class='fa fa-trash'></span>"));
        button.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("btn");
                oldClasses.add("btn-outline-danger");
                return oldClasses;
            }
        });
        button.add(new DisabledAttributeBehavior());
        return button;
    }

    public final AbstractLink createDeleteAjaxButton(String id, IModel<ENTITY> model) {
        AjaxLink link = new AjaxLink<ENTITY>(id, model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onRequestDelete(target, model);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                ENTITY e = getModelObject();
                this.setEnabled(e != null && !e.isNew());
            }
        };

        return link;
    }

    public final Class<ENTITY> getEntityClass() {
        return entityClass;
    }

    public Component createDetails(String id, IModel<ENTITY> entityiModel) {
        return new DefaultDetailsPanel<ENTITY>(id, entityiModel, entityClass);
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    public void addOnBack(Consumer<Optional<AjaxRequestTarget>> consumer) {
        this.onBack = onBack.andThen(consumer);
    }

    public Optional<ENTITY> getEntityFromPageParam() {
        return Optional.ofNullable(getPage().getPageParameters().get(0).toOptionalString()).map(str -> getConverter(entityClass).convertToObject(str, Session.get().getLocale()));
    }
}
