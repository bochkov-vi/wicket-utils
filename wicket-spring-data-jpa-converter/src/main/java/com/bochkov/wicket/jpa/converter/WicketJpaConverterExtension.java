package com.bochkov.wicket.jpa.converter;

import com.giffing.wicket.spring.boot.context.extensions.ApplicationInitExtension;
import com.giffing.wicket.spring.boot.context.extensions.WicketApplicationInitConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.convert.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaPersistableEntityInformation;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.Repositories;

@Slf4j
@ApplicationInitExtension
public class WicketJpaConverterExtension implements WicketApplicationInitConfiguration {

    @Autowired
    ApplicationContext context;

    public static void init(ConverterLocator converterLocator, ApplicationContext context) {
        Repositories repositories = new Repositories(context);
        repositories.forEach(clazz -> {
            log.info("Class = {}", clazz.getName());
            EntityInformation entityInformation = repositories.getEntityInformationFor(clazz);
            boolean composite = entityInformation instanceof JpaPersistableEntityInformation && ((JpaPersistableEntityInformation) entityInformation).hasCompositeId();

            Class entityClass = entityInformation.getJavaType();
            Class idClass = entityInformation.getIdType();
            JpaRepository jpaRepository = (JpaRepository) repositories.getRepositoryFor(entityClass).get();
            log.info("idClass = {}", idClass.getName());
            IConverter idConverter = converterLocator.get(idClass);
            if (idConverter == null && composite) {
                idConverter = new CompositeConverter(idClass);
                converterLocator.set(idClass, idConverter);
            }
            if(idConverter==null){
                idConverter = converterLocator.getConverter(idClass);
            }
            IConverter entityConverter = converterLocator.get(entityClass);
            if (entityConverter == null) {
                entityConverter = new WicketJpaConverter(entityClass, idConverter, jpaRepository);
                converterLocator.set(entityClass, entityConverter);
            }
        });
        log.info("{}", converterLocator);
    }

    @Override
    public void init(WebApplication webApplication) {
        init((ConverterLocator) webApplication.getConverterLocator(), context);
    }
}
