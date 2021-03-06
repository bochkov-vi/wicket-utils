package com.bochkov.wicket.data.provider;

import com.bochkov.wicket.jpa.model.PersistableModel;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableSupplier;
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

/**
 * The type Persistable data provider.
 *
 * @param <T>  the type parameter
 * @param <ID> the type parameter
 */
public abstract class PersistableDataProvider<T extends Persistable<ID>, ID extends Serializable> extends SortableDataProvider<T, String> {

    /**
     * The Size.
     */
    transient Long size = null;


    public static <T extends Persistable<ID>, ID extends Serializable, R extends JpaSpecificationExecutor<T> & CrudRepository<T, ID>> PersistableDataProvider<T, ID> of(SerializableSupplier<R> repository) {
        return new PersistableDataProvider<T, ID>() {
            @Override
            public R getRepository() {
                return repository.get();
            }
        };
    }

    public static <T extends Persistable<ID>, ID extends Serializable, R extends JpaSpecificationExecutor<T> & CrudRepository<T, ID>> PersistableDataProvider<T, ID> of(SerializableSupplier<R> repository, SerializableSupplier<Specification<T>> specification) {
        return new PersistableDataProvider<T, ID>() {
            @Override
            public R getRepository() {
                return repository.get();
            }

            @Override
            public Specification<T> createSpecification() {
                return specification.get();
            }
        };
    }

    public static <T extends Persistable<ID>, ID extends Serializable, R extends JpaSpecificationExecutor<T> & CrudRepository<T, ID>> PersistableDataProvider<T, ID> of(SerializableSupplier<R> repository, SerializableSupplier<Specification<T>> specification, SerializableSupplier<Sort> sort) {
        return new PersistableDataProvider<T, ID>() {
            @Override
            public R getRepository() {
                return repository.get();
            }

            @Override
            public Specification<T> createSpecification() {
                return specification.get();
            }

            @Override
            protected Sort getSort(SingleSortState<String> sortState) {
                return Sort.unsorted().and(Optional.ofNullable(super.getSort(sortState)).orElseGet(Sort::unsorted)).and(Optional.ofNullable(sort.get()).orElseGet(Sort::unsorted));
            }
        };
    }


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

    /**
     * Gets sort.
     *
     * @param sortState the sort state
     * @return the sort
     */
    protected Sort getSort(SingleSortState<String> sortState) {
        return Optional.of(sortState).map(SingleSortState::getSort).map(s -> Sort.by(s.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC, s.getProperty())).orElse(Sort.unsorted());
    }

    /**
     * Convert sort . order.
     *
     * @param sortParam the sort param
     * @return the sort . order
     */
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

    /**
     * Gets repository.
     *
     * @param <R> the type parameter
     * @return the repository
     */
    public abstract <R extends JpaSpecificationExecutor<T> & CrudRepository<T, ID>> R getRepository();

    /**
     * Create specification specification.
     *
     * @return the specification
     */
    public Specification<T> createSpecification() {
        return null;
    }
}
