package com.bochkov.wicket.data.model.nonser;

import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.io.Serializable;
import java.util.Optional;

public abstract class NonSerializableModel<ID, T> extends LoadableDetachableModel<T> {

    protected ID key;

    public static <ID extends Serializable, T> NonSerializableModel<ID, T> of(SerializableFunction<T, ID> packer, SerializableFunction<ID, Optional<T>> unpacker) {
        return new NonSerializableModel<ID, T>() {
            @Override
            public ID pack(T object) {
                return Optional.ofNullable(object).map(packer::apply).orElse(null);
            }

            @Override
            public T unpack(ID id) {
                return Optional.ofNullable(id).flatMap(unpacker::apply).orElse(null);
            }
        };
    }

    @Override
    protected T load() {
        T result = Optional.ofNullable(key).map(this::unpack).orElse(null);
        return result;
    }

    @Override
    public void setObject(T object) {
        super.setObject(object);
        this.key = Optional.ofNullable(object).map(this::pack).orElse(null);
    }

    public abstract ID pack(T object);

    public abstract T unpack(ID id);

    public NonSerializableModel<ID, T> setKey(ID key) {
        this.key = key;
        return this;
    }

    @Override
    public void onDetach() {
        this.key = pack(getObject());
    }
}
