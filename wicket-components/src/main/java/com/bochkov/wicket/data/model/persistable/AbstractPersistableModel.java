package com.bochkov.wicket.data.model.persistable;

import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.danekja.java.util.function.serializable.SerializableSupplier;

import java.io.Serializable;
import java.util.Optional;

public abstract class AbstractPersistableModel<T, ID extends Serializable> extends LoadableDetachableModel<T> {

     AbstractPersistableModel() {
    }

     AbstractPersistableModel(T object) {
        super(object);
    }

    public static <T, ID extends Serializable> AbstractPersistableModel<T, ID> of(SerializableSupplier<ID> keyGetter, SerializableConsumer<ID> keySetter, SerializableFunction<ID, Optional<T>> entityFinder, SerializableFunction<T, ID> keyExtractor) {
        return new AbstractPersistableModel<T, ID>() {
            @Override
            public Optional<T> findById(ID id) {
                return Optional.ofNullable(id).flatMap(entityFinder);
            }

            @Override
            ID getKeyFromStore() {
                return keyGetter.get();
            }

            @Override
            void setKeyToStore(ID id) {
                keySetter.accept(id);
            }

            @Override
            ID getKeyFromEntity(T entity) {
                return Optional.ofNullable(entity).map(keyExtractor).orElse(null);
            }
        };
    }

    public abstract Optional<T> findById(ID id);

    @Override
    protected T load() {
        return Optional.ofNullable(getId()).flatMap(this::findById).orElse(null);
    }

    abstract ID getKeyFromStore();

    abstract void setKeyToStore(ID id);

    abstract ID getKeyFromEntity(T entity);

    public ID getId() {
        return getKeyFromStore();
    }

    public AbstractPersistableModel<T, ID> setId(ID id) {
        setKeyToStore(id);
        return this;
    }

    @Override
    public void setObject(T object) {
        super.setObject(object);
    }

    @Override
    protected void onDetach() {
        setKeyToStore(getKeyFromEntity(getObject()));
    }
}
