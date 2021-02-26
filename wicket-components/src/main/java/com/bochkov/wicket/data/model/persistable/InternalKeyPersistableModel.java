package com.bochkov.wicket.data.model.persistable;

import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Optional;

public abstract class InternalKeyPersistableModel<T, ID extends Serializable> extends AbstractPersistableModel<T, ID> {

    ID key;

    InternalKeyPersistableModel() {
    }

    InternalKeyPersistableModel(T object) {
        super(object);
    }

    public static <T, ID extends Serializable> InternalKeyPersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityFinder, SerializableFunction<T, ID> keyExtractor) {
        return new InternalKeyPersistableModel<T, ID>() {
            @Override
            public Optional<T> findById(ID id) {
                return Optional.ofNullable(id).flatMap(entityFinder::apply);
            }

            @Override
            ID getKeyFromEntity(T entity) {
                return keyExtractor.apply(entity);
            }
        };
    }

    public static <T, ID extends Serializable> InternalKeyPersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityFinder, SerializableFunction<T, ID> keyExtractor, ID id) {
        InternalKeyPersistableModel<T, ID> model = new InternalKeyPersistableModel<T, ID>(entityFinder.apply(id).orElse(null)) {
            @Override
            public Optional<T> findById(ID id) {
                return Optional.ofNullable(id).flatMap(entityFinder::apply);
            }

            @Override
            ID getKeyFromEntity(T entity) {
                return keyExtractor.apply(entity);
            }
        };
        return model;
    }

    public static <T, ID extends Serializable> InternalKeyPersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityFinder, SerializableFunction<T, ID> keyExtractor, T entity) {
        InternalKeyPersistableModel<T, ID> model = new InternalKeyPersistableModel<T, ID>(entity) {
            @Override
            public Optional<T> findById(ID id) {
                return Optional.ofNullable(id).flatMap(entityFinder::apply);
            }

            @Override
            ID getKeyFromEntity(T entity) {
                return keyExtractor.apply(entity);
            }
        };
        model.setObject(entity);
        return model;
    }

    public static <T extends Persistable<ID>, ID extends Serializable> InternalKeyPersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityFinder, T entity) {
        return of(entityFinder, Persistable::getId, entity);
    }

    public static <T extends Persistable<ID>, ID extends Serializable> InternalKeyPersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityFinder, ID id) {
        return of(entityFinder, Persistable::getId, id);
    }

    public static <T extends Persistable<ID>, ID extends Serializable> InternalKeyPersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityFinder) {
        return of(entityFinder, (SerializableFunction<T, ID>) Persistable::getId);
    }

    @Override
    ID getKeyFromStore() {
        return key;
    }

    @Override
    void setKeyToStore(ID key) {
        this.key = key;
    }
}
