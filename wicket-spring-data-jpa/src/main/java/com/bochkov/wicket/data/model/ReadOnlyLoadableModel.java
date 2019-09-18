package com.bochkov.wicket.data.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.danekja.java.util.function.serializable.SerializableSupplier;

import java.util.Optional;

/**
 * The type Read only loadable model.
 *
 * @param <T>  the type parameter
 * @param <ID> the type parameter
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class ReadOnlyLoadableModel<T, ID> extends LoadableDetachableModel<T> {


    /**
     * Of read only loadable model.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param idSupplier   the id supplier
     * @param entityLoader the entity loader
     * @return the read only loadable model
     */
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

    /**
     * Find by id optional.
     *
     * @param id the id
     * @return the optional
     */
    public abstract Optional<T> findById(ID id);

    /**
     * Gets id.
     *
     * @return the id
     */
    public abstract ID getId();

    /**
     * If null get t.
     *
     * @return the t
     */
    public abstract T ifNullGet();
}
