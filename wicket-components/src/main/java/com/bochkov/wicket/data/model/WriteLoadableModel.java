package com.bochkov.wicket.data.model;

import org.danekja.java.util.function.serializable.SerializableFunction;

import java.io.Serializable;
import java.util.Optional;

/**
 * The type Write loadable model.
 *
 * @param <T>  the type parameter
 * @param <ID> the type parameter
 */
public abstract class WriteLoadableModel<T, ID extends Serializable> extends ReadOnlyLoadableModel<T, ID> {

    /**
     * The Id.
     */
    ID id;

    /**
     * Of write loadable model.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param idExtractor  the id extractor
     * @param entityLoader the entity loader
     * @return the write loadable model
     */
    public static <T, ID extends Serializable> WriteLoadableModel<T, ID> of(SerializableFunction<T, ID> idExtractor, SerializableFunction<ID, Optional<T>> entityLoader) {
        return new WriteLoadableModel<T, ID>() {
            @Override
            public Optional<T> findById(ID id) {
                return entityLoader.apply(id);
            }

            @Override
            protected ID extractId(T entity) {
                return idExtractor.apply(entity);
            }

            @Override
            public T ifNullGet() {
                return null;
            }
        };
    }

    /**
     * Of write loadable model.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param id           the id
     * @param idExtractor  the id extractor
     * @param entityLoader the entity loader
     * @return the write loadable model
     */
    public static <T, ID extends Serializable> WriteLoadableModel<T, ID> of(ID id, SerializableFunction<T, ID> idExtractor, SerializableFunction<ID, Optional<T>> entityLoader) {
        WriteLoadableModel<T, ID> model = of(idExtractor, entityLoader);
        model.setId(id);
        return model;
    }

    /**
     * Of write loadable model.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param entity       the entity
     * @param idExtractor  the id extractor
     * @param entityLoader the entity loader
     * @return the write loadable model
     */
    public static <T, ID extends Serializable> WriteLoadableModel<T, ID> of(T entity, SerializableFunction<T, ID> idExtractor, SerializableFunction<ID, Optional<T>> entityLoader) {
        WriteLoadableModel<T, ID> model = of(idExtractor, entityLoader);
        model.setObject(entity);
        return model;
    }

    @Override
    public void setObject(T object) {
        super.setObject(object);
        this.id = Optional.ofNullable(object).map(this::extractId).orElse(null);
    }

    public ID getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public WriteLoadableModel<T, ID> setId(ID id) {
        if (id != null) {
            setObject(findById(id).orElse(null));
        }else{
            setObject(null);
        }
        return this;
    }

    /**
     * Extract id id.
     *
     * @param entity the entity
     * @return the id
     */
    protected abstract ID extractId(T entity);

}
