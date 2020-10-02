package com.bochkov.wicket.component.select2.data;

import com.google.common.primitives.Ints;
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

import java.time.LocalDate;
import java.util.Optional;

public abstract class PersistableChoiceProvider<T extends Persistable<ID>, ID> extends MaskableChoiceProvider<T> {

    protected Class<ID> idClass;

    public PersistableChoiceProvider(Iterable<String> maskedProperties) {
        super(maskedProperties);
        idClass = (Class<ID>) getGeneric(1);
    }

    public PersistableChoiceProvider() {
        idClass = (Class<ID>) getGeneric(1);
    }

    public PersistableChoiceProvider(String... maskedProperties) {
        super(maskedProperties);
        idClass = (Class<ID>) getGeneric(1);
    }

    public static <T extends Persistable<ID>, ID, R extends JpaSpecificationExecutor<T> & JpaRepository<T, ID>> PersistableChoiceProvider<T, ID>
    of(SerializableSupplier<R> repositorySupplier,
       SerializableFunction<ID, String> toString,
       SerializableFunction<String, ID> toIdFunc,
       String... maskedProperty) {
        return new PersistableChoiceProvider<T, ID>(maskedProperty) {

            @Override
            public String idToString(ID object) {
                return Optional.ofNullable(object).map(toString).orElse(null);
            }

            public ID toId(String str) {
                return Optional.ofNullable(str).map(toIdFunc).orElse(null);
            }

            @Override
            public R getRepository() {
                return repositorySupplier.get();
            }
        };
    }

    public static <T extends Persistable<Integer>, R extends JpaSpecificationExecutor<T> & JpaRepository<T, Integer>> PersistableChoiceProvider<T, Integer>
    ofIntId(SerializableSupplier<R> repositorySupplier,
            String... maskedProperty) {
        return of(repositorySupplier, String::valueOf, Ints::tryParse, maskedProperty);
    }

    public static <T extends Persistable<Long>, R extends JpaSpecificationExecutor<T> & JpaRepository<T, Long>> PersistableChoiceProvider<T, Long>
    ofLongId(SerializableSupplier<R> repositorySupplier,
             String... maskedProperty) {
        return of(repositorySupplier, String::valueOf, id -> Optional.of(id).map(Ints::tryParse).map(Number::longValue).orElse(null), maskedProperty);
    }

    public static <T extends Persistable<LocalDate>, R extends JpaSpecificationExecutor<T> & JpaRepository<T, LocalDate>> PersistableChoiceProvider<T, LocalDate>
    ofLocalDateId(SerializableSupplier<R> repositorySupplier,
                  String... maskedProperty) {
        return of(repositorySupplier, Object::toString, id -> Optional.of(id).map(s -> {
            try {
                return LocalDate.parse(s);
            } catch (Exception e) {

            }
            return null;
        }).orElse(null), maskedProperty);
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
        return Optional.ofNullable(str).map(v -> Application.get().getConverterLocator().getConverter(idClass).convertToObject(v, Session.get().getLocale())).orElse(null);

    }

    public abstract <R extends JpaSpecificationExecutor<T> & JpaRepository<T, ID>> R getRepository();
}
