package com.bochkov.wicket.component.select2.data;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

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
        return getGeneric(0);
    }

    public Class<T> getGeneric(int i) {
        Class<T> persistentClass = null;
        try {
            persistentClass = (Class<T>)
                    ((ParameterizedType) getClass().getGenericSuperclass())
                            .getActualTypeArguments()[i];
        } catch (Exception e) {

        }
        return persistentClass;
    }

    @Override
    public T toChoise(String id) {
        return convertToObject(id);
    }

    @Override
    public String getIdValue(T object) {
        return convertToString(object);
    }

    public T convertToObject(String value) throws ConversionException {
        return getConverter().convertToObject(value, Session.get().getLocale());
    }

    public String convertToString(T value) {
        return getConverter().convertToString(value, Session.get().getLocale());
    }

    public IConverter<T> getConverter() {
        return Application.get().getConverterLocator().getConverter(_class);
    }
}
