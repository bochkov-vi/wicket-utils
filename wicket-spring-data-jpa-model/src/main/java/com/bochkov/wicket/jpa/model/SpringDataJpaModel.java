package com.bochkov.wicket.jpa.model;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.Repositories;

import java.io.Serializable;
import java.util.Optional;

public class SpringDataJpaModel<T, ID extends Serializable> extends NonSerializableModel<T, ID> {

    transient EntityInformation<T, ID> entityInformation;

    transient JpaRepository<T, ID> repository;

    @SpringBean
    transient ApplicationContext context;

    Class<T> entityClass;

    public SpringDataJpaModel(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public SpringDataJpaModel(T object, Class<T> entityClass) {
        super(object);
        this.entityClass = entityClass;
    }

    public SpringDataJpaModel(T object) {
        super(object);
        entityClass = (Class<T>) object.getClass();
    }

    @Override
    protected T load() {
        init();
        return super.load();
    }

    public void init() {
        Injector.get().inject(this);
        Repositories repositories = new Repositories(context);
        entityInformation = repositories.getEntityInformationFor(entityClass);
        repositories.getRepositoryFor(entityClass);
    }

    @Override
    public Optional<ID> pack(T entity) {
        return Optional.ofNullable(entity).map(e -> entityInformation.getId(e));
    }

    @Override
    public Optional<T> unpack(ID id) {
        return repository.findById(id);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        repository = null;
        entityInformation = null;
    }
}
