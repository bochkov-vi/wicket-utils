package com.bochkov.wicket.jpa.model;

import org.danekja.java.util.function.serializable.SerializableFunction;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;


public abstract class PersistableModel<T extends Persistable<ID>, ID extends Serializable> extends NonSerializableModel<T, ID> {


    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(ID id, SerializableFunction<ID, Optional<T>> entityLoader, SerializableSupplier<T> ifNullGet) {
        PersistableModel<T, ID> model = new PersistableModel<T, ID>() {
            @Override
            public Optional<T> unpack(ID id) {
                return Optional.ofNullable(id).map(pk -> entityLoader.apply(pk)).orElse(null);
            }

            @Override
            public T ifNullGet() {
                return Optional.ofNullable(ifNullGet).map(Supplier::get).orElse(null);
            }
        };
        model.setKey(id);
        return model;
    }


    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityLoader) {
        return of((ID) null, entityLoader, (SerializableSupplier<T>) null);
    }


    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(ID id, SerializableFunction<ID, Optional<T>> entityLoader) {
        PersistableModel<T, ID> model = of(entityLoader);
        model.setKey(id);
        return model;
    }

    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(SerializableFunction<ID, Optional<T>> entityLoader, SerializableSupplier<T> ifNullGet) {
        return of((ID) null, entityLoader, ifNullGet);
    }

    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(T entity, SerializableFunction<ID, Optional<T>> entityLoader, SerializableSupplier<T> ifNullGet) {
        return of(Optional.ofNullable(entity).map(Persistable::getId).orElse(null), entityLoader, ifNullGet);
    }

    public static <T extends Persistable<ID>, ID extends Serializable> PersistableModel<T, ID> of(T entity, SerializableFunction<ID, Optional<T>> entityLoader) {
        PersistableModel<T, ID> model = of(entityLoader);
        model.setObject(entity);
        return model;
    }

    @Override
    protected T load() {
        return Optional.ofNullable(key).flatMap(this::unpack).orElseGet(this::ifNullGet);
    }

    @Override
    public Optional<ID> pack(T entity) {
        return Optional.ofNullable(entity).map(Persistable::getId);
    }

    public T ifNullGet() {
        return null;
    }

    public PersistableModel<T, ID> copyWithIfNullGet(SerializableSupplier<T> ifNullGet) {
        PersistableModel<T, ID> _this = this;
        PersistableModel<T, ID> result = new PersistableModel<T, ID>() {
            @Override
            public Optional<T> unpack(ID id) {
                return _this.unpack(id);
            }

            @Override
            public void onDetach() {
                super.onDetach();
                _this.detach();
            }

            @Override
            public T ifNullGet() {
                return Optional.ofNullable(ifNullGet).map(Supplier::get).orElse(null);
            }
        };
        result.setObject(_this.getObject());
        return result;
    }
}
