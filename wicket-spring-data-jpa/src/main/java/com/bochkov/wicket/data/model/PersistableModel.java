package com.bochkov.wicket.data.model;

import org.danekja.java.util.function.serializable.SerializableFunction;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The type Persistable model.
 *
 * @param <T>  the type parameter
 * @param <ID> the type parameter
 */
public abstract class PersistableModel<T extends Persistable<ID>, ID extends Serializable> extends WriteLoadableModel<T, ID> {


    /**
     * Of persistable model.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param entityLoader the entity loader
     * @param ifNullGet    the if null get
     * @return the persistable model
     */
    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityLoader, SerializableSupplier<T> ifNullGet) {
        PersistableModel<T, ID> model = new PersistableModel<T, ID>() {
            @Override
            public Optional<T> findById(ID id) {
                return Optional.ofNullable(id).map(pk -> entityLoader.apply(pk)).orElse(null);
            }

            @Override
            public T ifNullGet() {
                return Optional.ofNullable(ifNullGet).map(Supplier::get).orElse(null);
            }
        };
        return model;
    }

    /**
     * Of persistable model.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param entityLoader the entity loader
     * @return the persistable model
     */
    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityLoader) {
        return of(entityLoader, (SerializableSupplier<T>) null);
    }

    /**
     * Of persistable model.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param id           the id
     * @param entityLoader the entity loader
     * @return the persistable model
     */
    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(ID id, SerializableFunction<ID, Optional<T>> entityLoader) {
        PersistableModel<T, ID> model = of(entityLoader);
        model.setId(id);
        return model;
    }

    /**
     * Of persistable model.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param entity       the entity
     * @param entityLoader the entity loader
     * @return the persistable model
     */
    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(T entity, SerializableFunction<ID, Optional<T>> entityLoader) {
        PersistableModel<T, ID> model = of(entityLoader);
        model.setObject(entity);
        return model;
    }

    @Override
    protected ID extractId(T entity) {
        return Optional.ofNullable(entity).map(Persistable::getId).orElse(null);
    }
}
