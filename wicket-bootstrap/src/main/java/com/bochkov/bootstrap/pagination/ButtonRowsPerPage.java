package com.bochkov.bootstrap.pagination;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.Arrays;

public class ButtonRowsPerPage extends Panel {
    Long[] rowsPerpage;
    DataTable table;

    public ButtonRowsPerPage(String id, DataTable table, Long... rowsPerPage) {
        super(id);
        this.table = table;
        this.rowsPerpage = rowsPerPage;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("drop-down-button", new LoadableDetachableModel<Long>() {
            @Override
            protected Long load() {
                return table.getItemsPerPage();
            }
        }));
        ListView listView = new ListView<Long>("rows-count-link", Arrays.asList(rowsPerpage)) {
            @Override
            protected void populateItem(ListItem<Long> item) {
                item.add(new Link<Long>("link", item.getModel()) {
                    @Override
                    public void onClick() {
                        table.setItemsPerPage(getModelObject());
                    }
                }.setBody(item.getModel()));
            }
        };
        add(listView);
    }
}
