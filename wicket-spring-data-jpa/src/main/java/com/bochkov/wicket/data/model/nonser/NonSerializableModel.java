package com.bochkov.wicket.data.model.nonser;

import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.io.Serializable;
import java.util.Optional;

public abstract class NonSerializableModel<ID , T> extends LoadableDetachableModel<T> {

    protected ID id;

    public static <ID extends Serializable, T> NonSerializableModel<ID, T> off(SerializableFunction<T, ID> packer, SerializableFunction<ID, T> unpacker) {
        return new NonSerializableModel<ID, T>() {
            @Override
            public ID pack(T object) {
                return packer.apply(object);
            }

            @Override
            public T unpack(ID id) {
                return unpacker.apply(id);
            }
        };
    }

    @Override
    protected T load() {
        return Optional.ofNullable(id).map(this::unpack).orElse(null);
    }

    @Override
    public void setObject(T object) {
        this.id = Optional.ofNullable(object).map(this::pack).orElse(null);
    }

    public abstract ID pack(T object);

    public abstract T unpack(ID id);
}
