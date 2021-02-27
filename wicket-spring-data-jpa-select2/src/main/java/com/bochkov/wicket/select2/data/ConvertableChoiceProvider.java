package com.bochkov.wicket.select2.data;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.danekja.java.util.function.serializable.SerializableSupplier;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class ConvertableChoiceProvider<T> extends PageableChoiceProvider<T> {

    private Class<T> _class;

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

    public Class<T> getGeneric(int index) {
        return (Class<T>) getGenericClassType(index);
    }

    private Type getGenericClassType(int index) {
        // To make it use generics without supplying the class type
        Type type = getClass().getGenericSuperclass();

        while (!(type instanceof ParameterizedType)) {
            if (type instanceof ParameterizedType) {
                type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
            } else {
                type = ((Class<?>) type).getGenericSuperclass();
            }
        }

        return ((ParameterizedType) type).getActualTypeArguments()[index];
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
        return getConverter(_class);
    }

    public <C> IConverter<C> getConverter(Class<C> _class) {
        return Application.get().getConverterLocator().getConverter(_class);
    }

    public <R> PageableChoiceProvider<R> map(SerializableFunction<T, R> mapper, Class<R> _class) {
        return super.map(mapper, r -> getConverter(_class).convertToString(r, Session.get().getLocale()));
    }

    public <R> PageableChoiceProvider<R> map(SerializableFunction<T, R> mapper, SerializableSupplier<IConverter<R>> converterSupplier) {
        return super.map(mapper, r -> converterSupplier.get().convertToString(r, Session.get().getLocale()));
    }

    public <R> PageableChoiceProvider<R> map(SerializableFunction<T, R> mapper, SerializableSupplier<IConverter<R>> converterSupplier, SerializableFunction<T, String> dispaly, SerializableFunction<R, T> inverseConverter) {
        return super.map(mapper, r -> converterSupplier.get().convertToString(r, Session.get().getLocale()), dispaly, inverseConverter);
    }
}
