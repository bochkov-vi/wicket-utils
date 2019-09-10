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

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public abstract class EntityChoiceProvider<T extends Persistable<ID>, ID extends Serializable> extends MaskableChoiceProvider<T, ID> {

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

    public static <T extends Persistable<ID>, ID extends Serializable> EntityChoiceProvider<T, ID> of(
            SerializableFunction<String, ID> idConverter,
            SerializableFunction<ID, Optional<T>> entityLoader,
            SerializableBiFunction<Specification<T>, Pageable, Page<T>> pageLoader,
            String... properties) {
        return of(idConverter, entityLoader, pageLoader, null, properties);
    }
}
