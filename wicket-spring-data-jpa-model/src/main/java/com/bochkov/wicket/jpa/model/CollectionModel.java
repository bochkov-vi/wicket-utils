package com.bochkov.wicket.jpa.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CollectionModel<T extends Persistable<ID>, ID extends Serializable, C extends Collection<T>> extends NonSerializableModel<C, Collection<ID>> {

    public CollectionModel() {
        super();
        setObject(createResultCollection(ImmutableList.<T>of().iterator()));
    }

    public CollectionModel(Iterable<T> elements) {
        super();
        setObject(createResultCollection(elements.iterator()));
    }

    public static <T extends Persistable<ID>, ID extends Serializable> CollectionModel<T, ID, Collection<T>> of(SerializableFunction<ID, Optional<T>> entityLoader) {
        return new CollectionModel<T, ID, Collection<T>>() {
            @Override
            public Optional<T> toEntity(ID id) {
                return Optional.ofNullable(id).map(entityLoader::apply).orElse(null);
            }
        };
    }

    public static <T extends Persistable<ID>, ID extends Serializable> CollectionModel<T, ID, Collection<T>> of(SerializableFunction<ID, Optional<T>> entityLoader, T... entity) {
        CollectionModel<T, ID, Collection<T>> result = of(entityLoader);
        result.setObject(Lists.newArrayList(entity));
        return result;
    }

    public static <T extends Persistable<ID>, ID extends Serializable> CollectionModel<T, ID, Collection<T>> of(SerializableFunction<ID, Optional<T>> entityLoader, Collection<T> entity) {
        CollectionModel<T, ID, Collection<T>> result = of(entityLoader);
        result.setObject(Lists.newArrayList(entity));
        return result;
    }

    public static <T extends Persistable<ID>, ID extends Serializable> CollectionModel<T, ID, Collection<T>> of(SerializableFunction<ID, Optional<T>> entityLoader, ID... id) {
        CollectionModel<T, ID, Collection<T>> result = of(entityLoader);
        result.setKey(Lists.newArrayList(id));
        return result;
    }


    public static <T extends Persistable<ID>, ID extends Serializable> CollectionModel<T, ID, List<T>> asList(SerializableFunction<ID, Optional<T>> entityLoader) {
        return new CollectionModel<T, ID, List<T>>() {
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

    public static <T extends Persistable<ID>, ID extends Serializable> CollectionModel<T, ID, List<T>> asList(SerializableFunction<ID, Optional<T>> entityLoader, Iterable<T> defaultValue) {
        return new CollectionModel<T, ID, List<T>>(defaultValue) {
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

    public static <T extends Persistable<ID>, ID extends Serializable> CollectionModel<T, ID, Set<T>> asSet(SerializableFunction<ID, Optional<T>> entityLoader) {
        return new CollectionModel<T, ID, Set<T>>() {
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
    public Optional<Collection<ID>> pack(C object) {
        Optional<Collection<ID>> result = Optional.ofNullable(object).map(Collection::stream).map(stream -> stream.filter(Objects::nonNull).map(this::toId).collect(Collectors.toList()));
        return result;
    }

    @Override
    public Optional<C> unpack(Collection<ID> ids) {
        Optional<C> result = null;
        result = Optional.ofNullable(ids).map(Collection::stream).map(stream -> stream.filter(Objects::nonNull).map(id -> this.toEntity(id).orElse(null)).filter(Objects::nonNull).iterator()).map(this::createResultCollection);
        return result;
    }

    public abstract Optional<T> toEntity(ID id);

    public ID toId(T entity) {
        return Optional.ofNullable(entity).map(Persistable::getId).orElse(null);
    }

    public C createResultCollection(Iterator<T> collection) {
        return (C) Lists.newArrayList(collection);
    }

    public IModel<Boolean> isPresent() {
        return new IModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return CollectionModel.this.getObject() != null && !CollectionModel.this.getObject().isEmpty();
            }

            @Override
            public void detach() {
                CollectionModel.this.detach();
            }
        };
    }
}
