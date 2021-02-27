package com.bochkov.wicket.jpa.crud.converter;

import com.google.common.collect.Lists;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class JpaConverterLocator extends ConverterLocator implements IConverterLocator {

    @Autowired
    ApplicationContext context;

    @PostConstruct
    public void postConstruct() {
        List<String> repos = Lists.newArrayList(context.getBeanNamesForType(JpaRepository.class));
        Repositories repositories = new Repositories(context);
        for (String name : repos) {
            Class<?> domainClass = repositories.getRepositoryInformation(context.getType(name)).get().getDomainType();
            Class<?> idClass = repositories.getRepositoryInformation(context.getType(name)).get().getIdType();
        }

    }
}
