package com.bochkov.wicket.data.provider;

import com.google.common.base.Strings;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.MapModel;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SortableListMapModelDataProvider<T extends Map<String, Object>> extends ListModelDataProvider<T> implements ISortableDataProvider<T, String> {

    private final SingleSortState<String> state = new SingleSortState<>();

    public SortableListMapModelDataProvider() {
    }

    public SortableListMapModelDataProvider(IModel<List<T>> list) {
        super(list);
    }

    public Stream<T> stream() {
        String sortedProperty = Optional.ofNullable(getSort()).map(SortParam::getProperty).orElse(null);
        if (!Strings.isNullOrEmpty(sortedProperty)) {
            boolean asc =getSort().isAscending();
            return super.stream().sorted((o1, o2) -> compare(o1, o2, sortedProperty,asc));
        }
        return super.stream();
    }

    @Override
    public IModel<T> model(T object) {
        return new MapModel(object);
    }

    @Override
    public final ISortState<String> getSortState() {
        return state;
    }

    /**
     * Returns current sort state
     *
     * @return current sort state
     */
    public SortParam<String> getSort() {
        return state.getSort();
    }

    /**
     * Sets the current sort state
     *
     * @param param parameter containing new sorting information
     */
    public void setSort(final SortParam<String> param) {
        state.setSort(param);
    }

    /**
     * Sets the current sort state
     *
     * @param property sort property
     * @param order    sort order
     */
    public void setSort(final String property, final SortOrder order) {
        state.setPropertySortOrder(property, order);
    }


    public int compare(Object o1, Object o2,boolean asc) {
        if (o1 instanceof Comparable && o2 instanceof Comparable) {
            Comparable c1 = (Comparable) o1;
            Comparable c2 = (Comparable) o2;
            return asc ? Comparator.<Comparable>naturalOrder().compare(c1, c2) : Comparator.<Comparable>reverseOrder().compare(c1, c2);
        }
        return 0;
    }

    public int compare(Map<String, Object> r1, Map<String, Object> r2, String property,boolean asc) {
        return compare(r1.get(property), r2.get(property),asc);
    }

}

