package com.bochkov.wicket.data.model.persistable;

import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public abstract class ChainedPersistableModel<T, ID extends Serializable> extends AbstractPersistableModel<T, ID> {

    IModel<ID> keyModel;

    ChainedPersistableModel() {
    }

    ChainedPersistableModel(T object) {
        super(object);
    }

    ChainedPersistableModel(IModel<ID> keyModel) {
        this.keyModel = keyModel;
    }

    ChainedPersistableModel(T object, IModel<ID> keyModel) {
        super(object);
        this.keyModel = keyModel;
    }

    public static <T, ID extends Serializable> ChainedPersistableModel<T, ID> of(IModel<ID> keyModel, SerializableFunction<ID, Optional<T>> entityFinder, SerializableFunction<T, ID> keyExtractor) {
        ChainedPersistableModel<T, ID> result = new ChainedPersistableModel<T, ID>(keyModel) {

            @Override
            public Optional<T> findById(ID serializable) {
                return entityFinder.apply(serializable);
            }

            @Override
            ID getKeyFromEntity(T entity) {
                return Optional.ofNullable(entity).map(keyExtractor).orElse(null);
            }
        };
        return result;
    }

    public static <T extends Persistable<ID>, ID extends Serializable> ChainedPersistableModel<T, ID> of(IModel<ID> keyModel, SerializableFunction<ID, Optional<T>> entityFinder) {
        ChainedPersistableModel<T, ID> result = ChainedPersistableModel.of(keyModel, entityFinder, (SerializableFunction<T, ID>) Persistable::getId);
        return result;
    }

    public static <T extends Persistable<ID>, ID extends Serializable> ChainedPersistableModel<T, ID> of(IModel<ID> keyModel, SerializableFunction<ID, Optional<T>> entityFinder, T entity) {
        ChainedPersistableModel<T, ID> result = ChainedPersistableModel.of(keyModel, entityFinder);
        result.setObject(entity);
        return result;
    }

    public static <T extends Persistable<ID>, ID extends Serializable> ChainedPersistableModel<T, ID> of(IModel<ID> keyModel, SerializableFunction<ID, Optional<T>> entityFinder, ID id) {
        ChainedPersistableModel<T, ID> result = ChainedPersistableModel.of(keyModel, entityFinder);
        keyModel.setObject(id);
        return result;
    }



    @Override
    ID getKeyFromStore() {
        return keyModel.getObject();
    }

    @Override
    void setKeyToStore(ID id) {
        keyModel.setObject(id);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        keyModel.detach();
    }
}
