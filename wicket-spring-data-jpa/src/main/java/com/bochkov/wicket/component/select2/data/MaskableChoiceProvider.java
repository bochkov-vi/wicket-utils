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
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.select2.Response;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * The type Maskable choice provider.
 *
 * @param <T>  the type parameter
 * @param <ID> the type parameter
 */
@Accessors(chain = true)
public abstract class MaskableChoiceProvider<T extends Persistable<ID>, ID extends Serializable> extends PageableChoiceProvider<T> implements Maskable {

    /**
     * The Masked properties.
     */
    @Getter
    @Setter
    Iterable<String> maskedProperties;

    public static <T extends Persistable<ID>, ID extends Serializable> MaskableChoiceProvider<T, ID> of(
            SerializableBiFunction<Specification<T>, Pageable, Page<T>> specificationExecutor,
            SerializableFunction<String, ID> idExtractor,
            SerializableFunction<ID, Optional<T>> loader,
            String... maskableProperties
    ) {
        MaskableChoiceProvider<T, ID> provider = new MaskableChoiceProvider<T, ID>() {
            @Override
            protected Page<T> findAll(Specification<T> specification, Pageable pageRequest) {
                return specificationExecutor.apply(specification, pageRequest);
            }

            @Override
            public ID toId(String str) {
                return idExtractor.apply(str);
            }

            @Override
            public Optional<? extends T> findById(ID id) {
                return loader.apply(id);
            }
        };
        provider.setMaskedProperties(Lists.newArrayList(maskableProperties));
        return provider;
    }


    @Override
    public String getDisplayValue(T object) {
        return Optional.ofNullable(object).map(T::toString).orElse("");
    }

    @Override
    public String getIdValue(T object) {
        return Optional.ofNullable(object).map(Persistable::getId).map(Object::toString).orElse("");
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
        return findAll(Optional.ofNullable(maskedSpecification).map(m -> m.and(excludeSpecification())).orElse(null), pageRequest);
    }

    /**
     * Find all page.
     *
     * @param specification the specification
     * @param pageRequest   the page request
     * @return the page
     */
    protected abstract Page<T> findAll(Specification<T> specification, Pageable pageRequest);

   /* @Override
    public Collection<T> toChoices(Collection<String> ids) {
        return ids.stream()
                .map(this::toId)
                .map(id -> findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }*/

    @Override
    public T toChoise(String id) {
        return Optional.ofNullable(id).map(this::toId).flatMap(this::findById).orElse(null);
    }

    /**
     * Gets masked properties.
     *
     * @return the masked properties
     */
    public Iterable<String> getMaskedProperties() {
        return maskedProperties;
    }

    /**
     * Sets masked properties.
     *
     * @param maskedProperties the masked properties
     * @return the masked properties
     */
    public MaskableChoiceProvider<T, ID> setMaskedProperties(Iterable<String> maskedProperties) {
        this.maskedProperties = maskedProperties;
        return this;
    }

    /**
     * To id id.
     *
     * @param str the str
     * @return the id
     */
    public abstract ID toId(String str);

    /**
     * Excludes list.
     *
     * @return the list
     */
    public List<T> excludes() {
        return null;
    }

    /**
     * Exclude specification specification.
     *
     * @return the specification
     */
    protected Specification<T> excludeSpecification() {
        List<T> excludes = excludes();
        if (excludes != null) {
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
    public abstract Optional<? extends T> findById(ID id);

}
