package com.bochkov.wicket.data.model;

import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Optional;

public abstract class PersistableDataProvider<T extends Persistable<ID>, ID extends Serializable> extends SortableDataProvider<T, String> {

    transient Long size = null;


    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        SingleSortState state = (SingleSortState) this.getSortState();
        Pageable pageRequest = PageRequest.of((int) (first / count), (int) count, getSort(state));
        return getRepository().findAll(createSpecification(), pageRequest).getContent().iterator();
    }

    @Override
    public long size() {
        if (size == null) {
            size = getRepository().count(createSpecification());
        }
        return size;
    }

    protected Sort getSort(SingleSortState<String> sortState) {
        return Optional.of(sortState).map(st -> st.getSort()).map(s -> new Sort(s.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC, s.getProperty())).orElse(Sort.unsorted());
    }

    protected Sort.Order convert(SortParam<String> sortParam) {
        return new Sort.Order(sortParam.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC, sortParam.getProperty());
    }

    @Override
    public IModel<T> model(T object) {
        return PersistableModel.of(object, id -> getRepository().findById(id));//new LoadableEntityModel<T, ID>(object, (id) -> getRepository().findById(id));
    }

    @Override
    public void detach() {
        size = null;
    }

    public abstract <R extends JpaSpecificationExecutor<T> & CrudRepository<T, ID>> R getRepository();

    public Specification<T> createSpecification() {
        return null;
    }
}
