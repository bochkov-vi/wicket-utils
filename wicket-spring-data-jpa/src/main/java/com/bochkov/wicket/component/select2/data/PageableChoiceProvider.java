package com.bochkov.wicket.component.select2.data;

import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The type Pageable choice provider.
 *
 * @param <T> the type parameter
 */
public abstract class PageableChoiceProvider<T> extends ChoiceProvider<T> {

    @Override
    public void query(String term, int page, Response<T> response) {
        Pageable pageRequest = PageRequest.of(page, getPageSize());
        Page<T> pageResponse = findByMask(term, pageRequest);
        response.setResults(pageResponse.getContent());
        response.setHasMore(pageResponse.hasNext());
    }

    @Override
    public String getDisplayValue(T object) {
        return Optional.ofNullable(object).map(String::valueOf).orElse(null);
    }


    @Override
    public Collection<T> toChoices(Collection<String> ids) {
        return Optional.ofNullable(ids).map(collection -> collection.stream().map(this::toChoise).filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    public abstract T toChoise(String id);

    /**
     * Find by mask page.
     *
     * @param term        the term
     * @param pageRequest the page request
     * @return the page
     */
    public abstract Page<T> findByMask(String term, Pageable pageRequest);

    /**
     * Gets page size.
     *
     * @return the page size
     */
    public int getPageSize() {
        return 10;
    }

    public <R> PageableChoiceProvider<R> map(SerializableFunction<T, R> mapper, SerializableFunction<R, String> idStringer) {
        PageableChoiceProvider<T> delegate = this;
        return new PageableChoiceProvider<R>() {

            @Override
            public R toChoise(String id) {
                return Optional.ofNullable(id).map(delegate::toChoise).map(mapper).orElse(null);
            }

            @Override
            public Page<R> findByMask(String term, Pageable pageRequest) {
                return delegate.findByMask(term, pageRequest).map(mapper);
            }

            @Override
            public String getIdValue(R object) {
                return Optional.ofNullable(object).map(idStringer).orElse(null);
            }
        };
    }
}
