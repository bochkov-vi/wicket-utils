package com.bochkov.wicket.component.select2.data;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.danekja.java.util.function.serializable.SerializableBiFunction;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.Optional;

/**
 * The type Entity choice provider.
 *
 * @param <T>  the type parameter
 * @param <ID> the type parameter
 */
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public abstract class EntityChoiceProvider<T extends Persistable<ID>, ID extends Serializable> extends MaskableChoiceProvider<T, ID> {

    public EntityChoiceProvider(String... properties) {
        super();
        setMaskedProperties(Lists.newArrayList(properties));
    }

    /**
     * Of entity choice provider.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param idConverter  the id converter
     * @param entityLoader the entity loader
     * @param pageLoader   the page loader
     * @param renderer     the renderer
     * @param properties   the properties
     * @return the entity choice provider
     */

    public static <T extends Persistable<ID>, ID extends Serializable> EntityChoiceProvider<T, ID> of(
            SerializableFunction<String, ID> idConverter,
            SerializableFunction<ID, Optional<T>> entityLoader,
            SerializableBiFunction<Specification<T>, Pageable, Page<T>> pageLoader,
            SerializableFunction<T, String> renderer,
            String... properties) {
        EntityChoiceProvider<T, ID> provider = new EntityChoiceProvider<T, ID>() {
            @Override
            protected Page<T> findAll(Specification<T> specification, Pageable pageRequest) {
                return pageLoader.apply(specification, pageRequest);
            }

            @Override
            public ID toId(String str) {
                ID id = null;
                try {
                    id = idConverter.apply(str);
                } catch (Exception e) {
                }
                return id;
            }

            @Override
            public String getDisplayValue(T object) {
                return Optional.ofNullable(renderer).map(r -> r.apply(object)).orElseGet(() -> super.getDisplayValue(object));
            }

            @Override
            public Optional<? extends T> findById(ID id) {
                return entityLoader.apply(id);
            }
        };
        provider.setMaskedProperties(Lists.newArrayList(properties));
        return provider;
    }


    /**
     * Of entity choice provider.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param idConverter  the id converter
     * @param entityLoader the entity loader
     * @param pageLoader   the page loader
     * @param properties   the properties
     * @return the entity choice provider
     */
    public static <T extends Persistable<ID>, ID extends Serializable> EntityChoiceProvider<T, ID> of(
            SerializableFunction<String, ID> idConverter,
            SerializableFunction<ID, Optional<T>> entityLoader,
            SerializableBiFunction<Specification<T>, Pageable, Page<T>> pageLoader,
            String... properties) {
        return of(idConverter, entityLoader, pageLoader, null, properties);
    }

    @Override
    public String getIdValue(T e) {
        return Optional.ofNullable(e).map(Persistable::getId).map(String::valueOf).orElse(null);
    }
}
