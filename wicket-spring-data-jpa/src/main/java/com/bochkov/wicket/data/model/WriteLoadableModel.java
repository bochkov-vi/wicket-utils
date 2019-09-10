package com.bochkov.wicket.data.model;

import org.danekja.java.util.function.serializable.SerializableFunction;

import java.io.Serializable;
import java.util.Optional;

public abstract class WriteLoadableModel<T, ID extends Serializable> extends ReadOnlyLoadableModel<T, ID> {

    ID id;

    public static <T, ID extends Serializable> WriteLoadableModel<T, ID> of(SerializableFunction<T, ID> idExtractor, SerializableFunction<ID, Optional<T>> entityLoader) {
        return new WriteLoadableModel<T, ID>() {
            @Override
            public Optional<T> findById(ID id) {
                return entityLoader.apply(id);
            }

            @Override
            protected ID extractId(T entity) {
                return idExtractor.apply(entity);
            }

            @Override
            public T ifNullGet() {
                return null;
            }
        };
    }

    public static <T, ID extends Serializable> WriteLoadableModel<T, ID> of(ID id, SerializableFunction<T, ID> idExtractor, SerializableFunction<ID, Optional<T>> entityLoader) {
        WriteLoadableModel<T, ID> model = of(idExtractor, entityLoader);
        model.setId(id);
        return model;
    }

    public static <T, ID extends Serializable> WriteLoadableModel<T, ID> of(T entity, SerializableFunction<T, ID> idExtractor, SerializableFunction<ID, Optional<T>> entityLoader) {
        WriteLoadableModel<T, ID> model = of(idExtractor, entityLoader);
        model.setObject(entity);
        return model;
    }

    @Override
    public void setObject(T object) {
        super.setObject(object);
        this.id = Optional.ofNullable(object).map(this::extractId).orElse(null);
    }

    public ID getId() {
        return id;
    }

    public WriteLoadableModel<T, ID> setId(ID id) {
        if (id != null) {
            setObject(findById(id).get());
        }else{
            setObject(null);
        }
        return this;
    }

    protected abstract ID extractId(T entity);

}
