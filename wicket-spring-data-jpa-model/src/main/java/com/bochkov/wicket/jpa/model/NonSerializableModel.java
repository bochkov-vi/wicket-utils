package com.bochkov.wicket.jpa.model;

import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.io.Serializable;
import java.util.Optional;

public abstract class NonSerializableModel<T, ID> extends LoadableDetachableModel<T> {

    protected ID key;

    public NonSerializableModel() {
    }

    public NonSerializableModel(T object) {
        super(object);
    }

    public static <ID extends Serializable, T> NonSerializableModel<T, ID> of(SerializableFunction<T, ID> packer, SerializableFunction<ID, T> unpacker) {
        return new NonSerializableModel<T, ID>() {
            @Override
            public Optional<ID> pack(T object) {
                return Optional.ofNullable(object).map(packer);
            }

            @Override
            public Optional<T> unpack(ID id) {
                return Optional.ofNullable(id).map(unpacker);
            }
        };
    }

    @Override
    protected T load() {
        return Optional.ofNullable(key).flatMap(this::unpack).orElse(null);
    }

    @Override
    public void setObject(T object) {
        super.setObject(object);
        this.key = Optional.ofNullable(object).flatMap(this::pack).orElse(null);
    }

    public abstract Optional<ID> pack(T object);

    public abstract Optional<T> unpack(ID id);

    public NonSerializableModel<T, ID> setKey(ID key) {
        this.key = key;
        return this;
    }

    @Override
    public void onDetach() {
        this.key = pack(getObject()).orElse(null);
    }
}
