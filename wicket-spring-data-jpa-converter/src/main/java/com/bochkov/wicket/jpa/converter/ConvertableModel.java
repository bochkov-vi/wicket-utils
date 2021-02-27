package com.bochkov.wicket.jpa.converter;

import com.bochkov.wicket.jpa.model.NonSerializableModel;
import org.apache.wicket.Application;
import org.apache.wicket.Session;

import java.util.Optional;

public class ConvertableModel<T> extends NonSerializableModel<T, String> {

    Class<T> entityClass;

    public ConvertableModel(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public ConvertableModel(T object, Class<T> entityClass) {
        super(object);
        this.entityClass = entityClass;
    }

    @Override
    public Optional<String> pack(T object) {
        return Optional.ofNullable(object).map(e -> Application.get().getConverterLocator().getConverter(entityClass).convertToString(e, Session.get().getLocale()));
    }

    @Override
    public Optional<T> unpack(String key) {
        return Optional.ofNullable(key).map(e -> Application.get().getConverterLocator().getConverter(entityClass).convertToObject(e, Session.get().getLocale()));
    }
}
