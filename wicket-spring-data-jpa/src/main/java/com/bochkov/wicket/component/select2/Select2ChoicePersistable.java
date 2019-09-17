package com.bochkov.wicket.component.select2;

import com.bochkov.wicket.component.select2.data.EntityChoiceProvider;
import org.danekja.java.util.function.serializable.SerializableBiFunction;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.select2.Select2Choice;
import org.wicketstuff.select2.Select2MultiChoice;

import java.io.Serializable;
import java.util.Optional;

/**
 * The type Select 2 choice persistable.
 *
 * @param <T>  the type parameter
 * @param <ID> the type parameter
 */
public class Select2ChoicePersistable<T extends Persistable<ID>, ID extends Serializable> {

    /**
     * Simple select 2 choice.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param id           the id
     * @param idConverter  the id converter
     * @param entityLoader the entity loader
     * @param pageLoader   the page loader
     * @param properties   the properties
     * @return the select 2 choice
     */
    public static <T extends Persistable<ID>, ID extends Serializable> Select2Choice<T> simple(
            String id,
            SerializableFunction<String, ID> idConverter,
            SerializableFunction<ID, Optional<T>> entityLoader,
            SerializableBiFunction<Specification<T>, Pageable, Page<T>> pageLoader,
            String... properties) {
        Select2Choice<T> select2 = new Select2Choice<>(id, EntityChoiceProvider.of(idConverter, entityLoader, pageLoader, properties));
        select2.getSettings().setTheme("bootstrap4");
        select2.getSettings().setCloseOnSelect(true);
        return select2;
    }

    /**
     * Multi select 2 multi choice.
     *
     * @param <T>          the type parameter
     * @param <ID>         the type parameter
     * @param id           the id
     * @param idConverter  the id converter
     * @param entityLoader the entity loader
     * @param pageLoader   the page loader
     * @param properties   the properties
     * @return the select 2 multi choice
     */
    public static <T extends Persistable<ID>, ID extends Serializable> Select2MultiChoice<T> multi(
            String id,
            SerializableFunction<String, ID> idConverter,
            SerializableFunction<ID, Optional<T>> entityLoader,
            SerializableBiFunction<Specification<T>, Pageable, Page<T>> pageLoader,
            String... properties) {
        Select2MultiChoice<T> select2 = new Select2MultiChoice<>(id, EntityChoiceProvider.of(idConverter, entityLoader, pageLoader, properties));
        select2.getSettings().setTheme("bootstrap4");
        select2.getSettings().setCloseOnSelect(true);
        return select2;
    }
}
