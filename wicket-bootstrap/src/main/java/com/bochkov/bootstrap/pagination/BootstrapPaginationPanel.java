package com.bochkov.bootstrap.pagination;

import com.google.common.collect.Lists;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.GenericWebMarkupContainer;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The type Bootstrap pagination panel.
 */
public class BootstrapPaginationPanel extends Panel {

    /**
     * The Table.
     */
    DataTable table;

    ButtonRowsPerPage buttonRowsPerPage;

    /**
     * The Page links count.
     */
    int pageLinksCount = 5;

    /**
     * Instantiates a new Bootstrap pagination panel.
     *
     * @param id    the id
     * @param table the table
     */
    public BootstrapPaginationPanel(String id, DataTable table, Long... perPage) {
        super(id);
        this.table = table;
        buttonRowsPerPage = new ButtonRowsPerPage("rows-per-page", table, perPage);
    }

    public BootstrapPaginationPanel(String id, DataTable table) {
        super(id);
        this.table = table;
        buttonRowsPerPage = new ButtonRowsPerPage("rows-per-page", table, 20L, 50L, 100L);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buttonRowsPerPage);
        add(createPageItem("first", Model.of(0l)));
        add(createPageItem("last", (IModel<Long>) () -> table.getPageCount() - 1));
        add(createPageItem("next", (IModel<Long>) () -> table.getCurrentPage() + 1));
        add(createPageItem("prev", (IModel<Long>) () -> table.getCurrentPage() - 1));
        add(new ListView<Long>("link-index", new IModel<List<Long>>() {
            @Override
            public List<Long> getObject() {
                final long pageCount = table.getPageCount();
                final long currentPage = table.getCurrentPage();
                long start = currentPage - pageLinksCount / 2;
                if (start < 0) {
                    start = 0;
                }
                if (start + pageLinksCount >= pageCount) {
                    start = pageCount - pageLinksCount;
                }
                List<Long> list = Lists.newArrayList();
                for (long i = 0; i < pageLinksCount; i++) {
                    if (start + i > pageCount) {
                        break;
                    }
                    if (start + i < 0) {
                        continue;
                    }
                    list.add(start + i);

                }
                return list;
            }
        }) {
            @Override
            protected void populateItem(ListItem<Long> item) {
                item.add(createPageLink("link", item.getModel()).setBody(item.getModel().map(i -> i + 1)));
                item.setVisible(item.getModel().map(pageIndex ->
                        pageIndex >= 0 && pageIndex < table.getPageCount()
                ).orElse(false).getObject());

                item.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {
                        if (item.getModel().map(lnkPage -> Objects.equals(lnkPage, table.getCurrentPage())).orElse(false).getObject()) {
                            oldClasses.add("active");
                        }
                        return oldClasses;
                    }
                });
            }
        });
    }

    /**
     * Create page item component.
     *
     * @param id   the id
     * @param page the page
     * @return the component
     */
    public Component createPageItem(String id, IModel<Long> page) {
        GenericWebMarkupContainer<Long> item = new GenericWebMarkupContainer<Long>(id, page) {
            boolean isActive() {
                return getModel().map(lnkPage -> Objects.equals(lnkPage, table.getCurrentPage())).orElse(false).getObject();
            }

            @Override
            public boolean isEnabled() {
                return getModel().map(pageIndex ->
                        pageIndex >= 0 && pageIndex < table.getPageCount()
                ).orElse(false).getObject();
            }

            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {
                        if (isActive()) {
                            oldClasses.add("active");
                        }
                        return oldClasses;
                    }
                });
            }
        };

        item.add(createPageLink("link", page));
        return item;
    }

    /**
     * Create page link abstract link.
     *
     * @param id   the id
     * @param page the page
     * @return the abstract link
     */
    public AbstractLink createPageLink(String id, IModel<Long> page) {
        Link<Long> link = new Link<Long>(id, page) {
            @Override
            public void onClick() {
                table.setCurrentPage(getModelObject());
            }
        };
        return link;
    }

}
