package com.bochkov.wicket.component.select2.data;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.danekja.java.util.function.serializable.SerializableBiFunction;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.select2.Response;

import java.util.Collection;
import java.util.Optional;


@Accessors(chain = true)
public abstract class MaskableChoiceProvider<T> extends ConvertableChoiceProvider<T> implements Maskable {

    /**
     * The Masked properties.
     */
    @Getter
    @Setter
    Iterable<String> maskedProperties;

    @Getter
    @Setter
    Specification<T> advancedSpecification;

    public MaskableChoiceProvider(Iterable<String> maskedProperties) {
        this.maskedProperties = maskedProperties;
    }

    public MaskableChoiceProvider() {
    }

    public MaskableChoiceProvider(Class<T> _class, Iterable<String> maskedProperties) {
        super(_class);
        this.maskedProperties = maskedProperties;
    }

    public MaskableChoiceProvider(String... maskedProperties) {
        this(Lists.newArrayList(maskedProperties));
    }

    public MaskableChoiceProvider(Class<T> _class, String... maskedProperties) {
        this(_class, Lists.newArrayList(maskedProperties));
    }

    public static <T> MaskableChoiceProvider<T> of(Class<T> _class,
                                                   SerializableFunction<T, String> toString,
                                                   SerializableBiFunction<Specification<T>, Pageable, Page<T>> pageLoader,
                                                   SerializableFunction<String, Optional<T>> entityLoader,
                                                   String... maskedProperty) {
        return new MaskableChoiceProvider<T>(_class, maskedProperty) {

            @Override
            public T toChoise(String id) {
                return Optional.ofNullable(id).flatMap(entityLoader).orElse(null);
            }

            @Override
            public String getIdValue(T object) {
                return Optional.ofNullable(object).map(toString).orElse(null);
            }

            @Override
            protected Page<T> findAll(Specification<T> specification, Pageable pageRequest) {
                return pageLoader.apply(specification, pageRequest);
            }
        };
    }

    public static <T> MaskableChoiceProvider<T> of(Class<T> _class, SerializableBiFunction<Specification<T>, Pageable, Page<T>> pageLoader,
                                                   String... maskedProperty) {
        MaskableChoiceProvider<T> provider = new MaskableChoiceProvider<T>(_class, maskedProperty) {
            @Override
            protected Page<T> findAll(Specification<T> specification, Pageable pageRequest) {
                return pageLoader.apply(specification, pageRequest);
            }
        };
        return provider;
    }


    @Override
    public String getDisplayValue(T object) {
        return Optional.ofNullable(object).map(T::toString).orElse("");
    }


    @Override
    public void query(String term, int page, Response<T> response) {
        Pageable pageRequest = PageRequest.of(page, getPageSize());
        Page<T> pageResponse = findByMask(term, pageRequest);
        response.setResults(pageResponse.getContent());
        response.setHasMore(pageResponse.hasNext());
    }

    public Page<T> findByMask(String term, Pageable pageRequest) {
        String expression = Optional.ofNullable(term).filter(s -> !Strings.isNullOrEmpty(s)).orElse("%");
        Specification<T> maskedSpecification = Maskable.maskSpecification(expression, maskedProperties);
        return findAll(Optional.ofNullable(maskedSpecification)
                .map(m -> m.and(excludeSpecification()))
                .map(m -> m.and(advancedSpecification))
                .orElse(null), pageRequest);
    }

   /* @Override
    public Collection<T> toChoices(Collection<String> ids) {
        return ids.stream()
                .map(this::toId)
                .map(id -> findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }*/

    /**
     * Find all page.
     *
     * @param specification the specification
     * @param pageRequest   the page request
     * @return the page
     */
    protected abstract Page<T> findAll(Specification<T> specification, Pageable pageRequest);

   /* @Override
    public T toChoise(String id) {
        return Optional.ofNullable(id).map(this::toId).flatMap(this::findById).orElse(null);
    }*/


    /**
     * To id id.
     *
     * @param str the str
     * @return the id
     */
    //public abstract ID toId(String str);

    /**
     * Excludes list.
     *
     * @return the list
     */
    public Collection<T> excludes() {
        return null;
    }

    /**
     * Exclude specification specification.
     *
     * @return the specification
     */
    protected Specification<T> excludeSpecification() {
        Collection<T> excludes = excludes();
        if (excludes != null && !excludes.isEmpty()) {
            return ((root, q, b) -> b.not(root.in(excludes)));
        }
        return null;
    }

    /**
     * Find by id optional.
     *
     * @param id the id
     * @return the optional
     */
    //public abstract Optional<? extends T> findById(ID id);
}
