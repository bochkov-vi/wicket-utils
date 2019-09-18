package com.bochkov.bootstrap.pagination;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 * The type Bootstrap navigation toolbar.
 */
public class BootstrapNavigationToolbar extends AbstractToolbar {
    /**
     * Instantiates a new Bootstrap navigation toolbar.
     *
     * @param table the table
     */
    public BootstrapNavigationToolbar(DataTable<?, ?> table) {
        super(table);
        WebMarkupContainer span = new WebMarkupContainer("span");
        add(span);
        span.add(AttributeModifier.replace("colspan", new IModel<String>() {
            @Override
            public String getObject() {
                return String.valueOf(table.getColumns().size()).intern();
            }
        }));

        span.add(newPagingNavigator("navigator", table));
    }

    /**
     * New paging navigator component.
     *
     * @param navigatorId the navigator id
     * @param table       the table
     * @return the component
     */
    protected Component newPagingNavigator(final String navigatorId,
                                           final DataTable<?, ?> table) {
        return new BootstrapPaginationPanel(navigatorId, table);
    }
}