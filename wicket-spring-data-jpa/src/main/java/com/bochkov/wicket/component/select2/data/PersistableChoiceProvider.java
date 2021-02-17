package com.bochkov.wicket.component.select2.data;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public abstract class PersistableChoiceProvider<T extends Persistable<ID>, ID> extends MaskableChoiceProvider<T> {

    protected Class<ID> idClass;

    public PersistableChoiceProvider(Class<ID> idClass, Iterable<String> maskedProperties) {
        super(maskedProperties);
        this.idClass = idClass;
    }

    public PersistableChoiceProvider(Class<ID> idClass) {
        this.idClass = idClass;
    }

    public PersistableChoiceProvider(Class<T> _class, Class<ID> idClass, Iterable<String> maskedProperties) {
        super(_class, maskedProperties);
        this.idClass = idClass;
    }

    public PersistableChoiceProvider(Class<ID> idClass, String... maskedProperties) {
        super(maskedProperties);
        this.idClass = idClass;
    }

    public PersistableChoiceProvider(Class<T> _class, Class<ID> idClass, String... maskedProperties) {
        super(_class, maskedProperties);
        this.idClass = idClass;
    }


    public static <T extends Persistable<ID>, ID, R extends JpaSpecificationExecutor<T> & JpaRepository<T, ID>> PersistableChoiceProvider<T, ID>
    of(Class<T> _class, Class<ID> idClass, SerializableSupplier<R> repositorySupplier,
       String... maskedProperty) {
        PersistableChoiceProvider<T, ID> provider = new PersistableChoiceProvider<T, ID>(_class, idClass, maskedProperty) {
            @Override
            public R getRepository() {
                return repositorySupplier.get();
            }
        };
        return provider;
    }

    @Override
    protected Page<T> findAll(Specification<T> specification, Pageable pageRequest) {
        return getRepository().findAll(specification, pageRequest);
    }

    @Override
    final public String getIdValue(T object) {
        return Optional.ofNullable(object).map(Persistable::getId).map(this::idToString).orElse(null);
    }

    public String idToString(ID id) {
        return Optional.ofNullable(id).map(pk -> Application.get().getConverterLocator().getConverter(idClass).convertToString(pk, Session.get().getLocale())).orElse(null);
    }

    public Optional<? extends T> findById(ID id) {
        return getRepository().findById(id);
    }

    @Override
    public final T toChoise(String id) {
        return Optional.ofNullable(id).map(this::toId).flatMap(pk -> getRepository().findById(pk)).orElse(null);
    }

    public ID toId(String str) {
        return Optional.ofNullable(str).map(v -> getConverter(idClass).convertToObject(v, Session.get().getLocale())).orElse(null);

    }

    public abstract <R extends JpaSpecificationExecutor<T> & JpaRepository<T, ID>> R getRepository();

    public PageableChoiceProvider<ID> map() {
        return map(String::valueOf);
    }

    public PageableChoiceProvider<ID> map(SerializableFunction<T, String> display) {
        return super.map(Persistable::getId, () -> getConverter(idClass), display, id -> findById(id).orElse(null));
    }
}
