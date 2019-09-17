package com.bochkov.wicket.data.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.model.LoadableDetachableModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * The type Page data model.
 *
 * @param <T> the type parameter
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class PageDataModel<T> extends LoadableDetachableModel<Page<T>> {

    /**
     * The First.
     */
    int first = 0;

    /**
     * The Count.
     */
    int count = 10;


    @Override
    protected Page<T> load() {
        return load(getSpecification(), getPageRequest());
    }

    /**
     * Load page.
     *
     * @param specification the specification
     * @param pageable      the pageable
     * @return the page
     */
    protected abstract Page<T> load(Specification<T> specification, Pageable pageable);

    /**
     * Gets specification.
     *
     * @return the specification
     */
    protected Specification<T> getSpecification() {
        return null;
    }

    /**
     * Gets page request.
     *
     * @return the page request
     */
    protected Pageable getPageRequest() {
        Sort sort = getSort();
        if (sort != null) {
            return PageRequest.of((int) (first / count), (int) count, sort);
        } else {
            return PageRequest.of((int) (first / count), (int) count);
        }
    }

    /**
     * Gets sort.
     *
     * @return the sort
     */
    protected Sort getSort() {
        return null;
    }
}
