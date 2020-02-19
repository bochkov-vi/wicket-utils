package com.bochkov.bootstrap.pagination;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import java.util.Arrays;

/**
 * The type Button rows per page.
 */
public class ButtonRowsPerPage extends Panel {

    /**
     * The Rows perpage.
     */
    Long[] rowsPerpage;

    /**
     * The Table.
     */
    DataTable table;

    /**
     * Instantiates a new Button rows per page.
     *
     * @param id          the id
     * @param table       the table
     * @param rowsPerPage the rows per page
     */
    public ButtonRowsPerPage(String id, DataTable table, Long... rowsPerPage) {
        super(id);
        this.table = table;
        this.rowsPerpage = rowsPerPage;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("drop-down-button", new StringResourceModel("perpage.template", this, Model.of(table))));
        ListView listView = new ListView<Long>("rows-count-link", Arrays.asList(rowsPerpage)) {
            @Override
            protected void populateItem(ListItem<Long> item) {
                Link link = new Link<Long>("link", item.getModel()) {
                    @Override
                    public void onClick() {
                        table.setItemsPerPage(getModelObject());
                    }
                };
                link.add(new Label("label", item.getModel()));
                item.add(link);
            }
        };
        add(listView);
    }
}
