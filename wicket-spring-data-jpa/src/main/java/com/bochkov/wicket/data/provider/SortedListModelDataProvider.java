package com.bochkov.wicket.data.provider;

import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class SortedListModelDataProvider<T> extends ListModelDataProvider<T> implements ISortableDataProvider<T, String> {

    private static final long serialVersionUID = 1L;
    private final SingleSortState<String> state = new SingleSortState<>();

    @Override
    public Iterator<T> iterator(final long first, final long count) {
        Stream<T> stream = stream();
        Optional<SortParam<String>> sort = Optional.ofNullable(getSort());
        Stream<T> sorted = sort.map(s -> comparator(s.getProperty(), s.isAscending())).map(cmp -> stream.sorted(cmp)).orElse(stream);
        return sorted.skip(first).limit(count).iterator();
    }

    @Override
    public final ISortState<String> getSortState() {
        return state;
    }

    Comparator<T> comparator(String property, boolean asc) {
        Comparator<T> result = Comparator.nullsLast(Comparator.comparing(o -> (Comparable) PropertyResolver.getValue(property, o)));
        if (!asc) {
            result = result.reversed();
        }
        return result;
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

}
