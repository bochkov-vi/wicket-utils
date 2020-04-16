package com.bochkov.wicket.component.select2.data;

import org.apache.wicket.Application;

import java.lang.reflect.ParameterizedType;


public abstract class ConvertableChoiceProvider<T> extends PageableChoiceProvider<T> {

    Class<T> _class;

    public ConvertableChoiceProvider(Class<T> _class) {
        super();
        this._class = _class;
    }

    public ConvertableChoiceProvider() {
        super();
        _class = (Class<T>) getGeneric();
    }

    public Class<T> getGeneric() {
        Class<T> persistentClass = (Class<T>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        return persistentClass;
    }

    @Override
    public T toChoise(String id) {
        return Application.get().getConverterLocator().getConverter(_class).convertToObject(id, null);
    }

    @Override
    public String getIdValue(T object) {
        return Application.get().getConverterLocator().getConverter(_class).convertToString(object, null);
    }

}
