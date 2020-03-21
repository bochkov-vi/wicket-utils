package com.bochkov.wicket.component.select2.data;

import com.google.common.primitives.Ints;
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

public abstract class PersistableChoiseProvider<T extends Persistable<ID>, ID> extends MaskableChoiceProvider<T, ID> {

    public PersistableChoiseProvider(Iterable<String> maskedProperties) {
        super(maskedProperties);
    }

    public PersistableChoiseProvider() {
    }

    public PersistableChoiseProvider(String... maskedProperties) {
        super(maskedProperties);
    }

    public static <T extends Persistable<ID>, ID, R extends JpaSpecificationExecutor<T> & JpaRepository<T, ID>> PersistableChoiseProvider<T, ID>
    of(SerializableSupplier<R> repositorySupplier,
       SerializableFunction<ID, String> toString,
       SerializableFunction<String, ID> toIdFunc,
       String... maskedProperty) {
        return new PersistableChoiseProvider<T, ID>(maskedProperty) {

            @Override
            public String idToString(ID object) {
                return Optional.ofNullable(object).map(toString).orElse(null);
            }

            @Override
            public ID toId(String str) {
                return Optional.ofNullable(str).map(toIdFunc).orElse(null);
            }

            @Override
            public R getRepository() {
                return repositorySupplier.get();
            }
        };
    }

    public static <T extends Persistable<Integer>, R extends JpaSpecificationExecutor<T> & JpaRepository<T, Integer>> PersistableChoiseProvider<T, Integer>
    ofIntId(SerializableSupplier<R> repositorySupplier,
            String... maskedProperty) {
        return of(repositorySupplier, String::valueOf, Ints::tryParse, maskedProperty);
    }

    public static <T extends Persistable<Long>, R extends JpaSpecificationExecutor<T> & JpaRepository<T, Long>> PersistableChoiseProvider<T, Long>
    ofLongId(SerializableSupplier<R> repositorySupplier,
             String... maskedProperty) {
        return of(repositorySupplier, String::valueOf, id -> Optional.of(id).map(Ints::tryParse).map(Number::longValue).orElse(null), maskedProperty);
    }

    public static <T extends Persistable<LocalDate>, R extends JpaSpecificationExecutor<T> & JpaRepository<T, LocalDate>> PersistableChoiseProvider<T, LocalDate>
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

    public abstract String idToString(ID id);

    @Override
    public Optional<? extends T> findById(ID id) {
        return getRepository().findById(id);
    }

    public abstract <R extends JpaSpecificationExecutor<T> & JpaRepository<T, ID>> R getRepository();
}
