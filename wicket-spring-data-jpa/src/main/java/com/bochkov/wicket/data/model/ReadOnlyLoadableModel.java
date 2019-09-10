package com.bochkov.wicket.data.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.danekja.java.util.function.serializable.SerializableSupplier;

import java.util.Optional;

@Getter
@Setter
@Accessors(chain = true)
public abstract class ReadOnlyLoadableModel<T, ID> extends LoadableDetachableModel<T> {


    public static <T, ID> ReadOnlyLoadableModel<T, ID> of(SerializableSupplier<ID> idSupplier, SerializableFunction<ID, Optional<T>> entityLoader) {
        return new ReadOnlyLoadableModel<T, ID>() {

            @Override
            public Optional<T> findById(ID id) {
                return entityLoader.apply(id);
            }

            @Override
            public ID getId() {
                return idSupplier.get();
            }

            @Override
            public T ifNullGet() {
                return null;
            }
        };
    }

    protected T load() {
        return Optional.ofNullable(getId()).flatMap(this::findById).orElse(ifNullGet());
    }

    @Override
    public void setObject(T object) {
        super.setObject(object);
    }

    public abstract Optional<T> findById(ID id);

    public abstract ID getId();

    public abstract T ifNullGet();
}
