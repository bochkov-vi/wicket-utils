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

@Getter
@Setter
@Accessors(chain = true)
public abstract class PageDataModel<T> extends LoadableDetachableModel<Page<T>> {

    int first = 0;

    int count = 10;


    @Override
    protected Page<T> load() {
        return load(getSpecification(), getPageRequest());
    }

    protected abstract Page<T> load(Specification<T> specification, Pageable pageable);

    protected Specification<T> getSpecification() {
        return null;
    }

    protected Pageable getPageRequest() {
        Sort sort = getSort();
        if (sort != null) {
            return PageRequest.of((int) (first / count), (int) count, sort);
        } else {
            return PageRequest.of((int) (first / count), (int) count);
        }
    }

    protected Sort getSort() {
        return null;
    }
}
