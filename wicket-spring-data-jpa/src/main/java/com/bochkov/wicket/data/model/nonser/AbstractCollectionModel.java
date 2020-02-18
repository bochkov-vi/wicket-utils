package com.bochkov.wicket.data.model.nonser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.*;

public abstract class AbstractCollectionModel<T extends Persistable<ID>, ID extends Serializable, C extends Collection<T>> extends NonSerializableModel<Collection<ID>, C> {

    public static <T extends Persistable<ID>, ID extends Serializable> AbstractCollectionModel<T, ID, Collection<T>> off(SerializableFunction<ID, Optional<T>> entityLoader) {
        return new AbstractCollectionModel<T, ID, Collection<T>>() {
            @Override
            public Optional<T> toEntity(ID id) {
                return Optional.ofNullable(id).map(entityLoader::apply).orElse(null);
            }
        };
    }

    public static <T extends Persistable<ID>, ID extends Serializable> AbstractCollectionModel<T, ID, List<T>> list(SerializableFunction<ID, Optional<T>> entityLoader) {
        return new AbstractCollectionModel<T, ID, List<T>>() {
            @Override
            public Optional<T> toEntity(ID id) {
                return Optional.ofNullable(id).map(entityLoader::apply).orElse(null);
            }

            @Override
            public List<T> createResultCollection(Iterator<T> collection) {
                return Lists.newArrayList(collection);
            }
        };
    }

    public static <T extends Persistable<ID>, ID extends Serializable> AbstractCollectionModel<T, ID, Set<T>> set(SerializableFunction<ID, Optional<T>> entityLoader) {
        return new AbstractCollectionModel<T, ID, Set<T>>() {
            @Override
            public Optional<T> toEntity(ID id) {
                return Optional.ofNullable(id).map(entityLoader::apply).orElse(null);
            }

            @Override
            public Set<T> createResultCollection(Iterator<T> collection) {
                return Sets.newHashSet(collection);
            }
        };
    }

    @Override
    public Collection<ID> pack(C object) {
        return Optional.ofNullable(object).map(Collection::stream).map(stream -> stream.filter(Objects::nonNull).map(this::toId).iterator()).map(Lists::newArrayList).orElse(null);
    }

    @Override
    public C unpack(Collection<ID> ids) {
        C result = null;
        result = (C) Optional.ofNullable(ids).map(Collection::stream).map(stream -> stream.filter(Objects::nonNull).map(id -> this.toEntity(id).orElse(null)).filter(Objects::nonNull).iterator()).map(this::createResultCollection).orElse(null);
        return result;
    }

    public abstract Optional<T> toEntity(ID id);

    public ID toId(T entity) {
        return Optional.ofNullable(entity).map(Persistable::getId).orElse(null);
    }

    public C createResultCollection(Iterator<T> collection) {
        return (C) Lists.newArrayList(collection);
    }
}
