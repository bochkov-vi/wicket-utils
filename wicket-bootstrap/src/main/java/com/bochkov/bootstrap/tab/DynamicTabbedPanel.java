/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bochkov.bootstrap.tab;

import com.bochkov.bootstrap.ActiveLinkBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;

import java.util.Collection;
import java.util.List;


/**
 * TabbedPanel component represents a panel with tabs that are used to switch between different
 * content panels inside the TabbedPanel panel.
 * <p>
 * <b>Note:</b> When the currently selected tab is replaced by changing the underlying list of tabs,
 * the change is not picked up unless a call is made to {@link #setSelectedTab(int)}.
 * <p>
 * Example:
 *
 * <pre>
 * List tabs=new ArrayList();
 * tabs.add(new AbstractTab(new Model&lt;String&gt;(&quot;first tab&quot;)) {
 *   public Panel getPanel(String panelId)
 *   {
 *     return new TabPanel1(panelId);
 *   }
 * });
 *
 * tabs.add(new AbstractTab(new Model&lt;String&gt;(&quot;second tab&quot;)) {
 *   public Panel getPanel(String panelId)
 *   {
 *     return new TabPanel2(panelId);
 *   }
 * });
 *
 * add(new TabbedPanel(&quot;tabs&quot;, tabs));
 *
 * &lt;span wicket:id=&quot;tabs&quot; class=&quot;tabpanel&quot;&gt;[tabbed panel will be here]&lt;/span&gt;
 * </pre>
 * <p>
 * For a complete example see the component references in wicket-examples project
 *
 * @param <T> The type of panel to be used for this component's tabs. Just use {@link ITab} if you
 *            have no special needs here.
 * @author Igor Vaynberg (ivaynberg at apache dot org)
 * @see ITab
 */
public class DynamicTabbedPanel<T extends ITab> extends Panel {

    /**
     * id used for child panels
     */
    public static final String TAB_PANEL_ID = "panel";

    private static final long serialVersionUID = 1L;

    private final IModel<List<T>> tabs;

    /**
     * the current tab
     */
    private int currentTab = -1;


    /**
     * Constructor
     *
     * @param id   component id
     * @param tabs list of ITab objects used to represent tabs
     */
    public DynamicTabbedPanel(final String id, final IModel<List<T>> tabs) {
        this(id, tabs, null);
    }

    /**
     * Constructor
     *
     * @param id    component id
     * @param tabs  list of ITab objects used to represent tabs
     * @param model model holding the index of the selected tab
     */
    public DynamicTabbedPanel(final String id, final IModel<List<T>> tabs, IModel<Integer> model) {
        super(id, model);

        this.tabs = Args.notNull(tabs, "tabs");

        final IModel<Integer> tabCount = new IModel<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getObject() {
                return tabs.map(Collection::size).getObject();
            }
        };

        WebMarkupContainer tabsContainer = newTabsContainer("tabs-container");
        add(tabsContainer);

        // add the loop used to generate tab names
        tabsContainer.add(new ListView<T>("tabs", tabs) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem item) {
                final int index = item.getIndex();
                final T tab = tabs.map(l -> l.get(index)).getObject();
                final WebMarkupContainer titleLink = newLink("link", index);
                titleLink.add(newTitle("title", tab.getTitle(), index));
                item.add(titleLink);
            }

            @Override
            protected ListItem newItem(int index, IModel itemModel) {
                return newTabContainer(index, itemModel);
            }


        });

        add(newPanel());
    }

    /**
     * Override of the default initModel behaviour. This component <strong>will not</strong> use any
     * compound model of a parent.
     *
     * @see Component#initModel()
     */
    @Override
    protected IModel<?> initModel() {
        return new Model<>(-1);
    }


    protected WebMarkupContainer newTabsContainer(final String id) {
        return new WebMarkupContainer(id);
    }

    /**
     * Generates a loop item used to represent a specific tab's <code>li</code> element.
     *
     * @param tabIndex
     * @return new loop item
     */
    protected ListItem<T> newTabContainer(final int tabIndex, IModel<T> itemModel) {
        return new ListItem<T>(tabIndex, itemModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onConfigure() {
                super.onConfigure();
                //setVisible(getVisiblityCache().isVisible(tabIndex));
            }
        };
    }

    @Override
    protected void onBeforeRender() {
        int index = getSelectedTab();
        setCurrentTab(index);
        super.onBeforeRender();
    }


    /**
     * @return list of tabs that can be used by the user to add/remove/reorder tabs in the panel
     */
    public final IModel<List<T>> getTabs() {
        return tabs;
    }

    /**
     * Factory method for tab titles. Returned component can be anything that can attach to span
     * tags such as a fragment, panel, or a label
     *
     * @param titleId    id of title component
     * @param titleModel model containing tab title
     * @param index      index of tab
     * @return title component
     */
    protected Component newTitle(final String titleId, final IModel<?> titleModel, final int index) {
        return new Label(titleId, titleModel);
    }

    /**
     * Factory method for links used to switch between tabs.
     * <p>
     * The created component is attached to the following markup. Label component with id: title
     * will be added for you by the tabbed panel.
     *
     * <pre>
     * &lt;a href=&quot;#&quot; wicket:id=&quot;link&quot;&gt;&lt;span wicket:id=&quot;title&quot;&gt;[[tab title]]&lt;/span&gt;&lt;/a&gt;
     * </pre>
     * <p>
     * Example implementation:
     *
     * <pre>
     * protected WebMarkupContainer newLink(String linkId, final int index)
     * {
     * 	return new Link(linkId)
     *    {
     * 		private static final long serialVersionUID = 1L;
     *
     * 		public void onClick()
     *        {
     * 			setSelectedTab(index);
     *        }
     *    };
     * }
     * </pre>
     *
     * @param linkId component id with which the link should be created
     * @param index  index of the tab that should be activated when this link is clicked. See
     *               {@link #setSelectedTab(int)}.
     * @return created link component
     */
    protected WebMarkupContainer newLink(final String linkId, final int index) {
        return (WebMarkupContainer) new Link<Void>(linkId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setSelectedTab(index);
            }
        }.add(new ActiveLinkBehavior() {
            @Override
            public boolean isActive(Component component) {
                return getSelectedTab() == index;
            }
        });
    }

    private void setCurrentTab(int index) {
        if (this.currentTab == index) {
            // already current
            return;
        }
        this.currentTab = index;

        final Component component;

        component = tabs
                .filter(l -> l.size() > index)
                .map(l -> l.get(index).getPanel(TAB_PANEL_ID))
                .orElseGet(() -> newPanel()).getObject();

        if (!component.getId().equals(TAB_PANEL_ID)) {
            throw new WicketRuntimeException(
                    "ITab.getPanel() returned a panel with invalid id [" +
                            component.getId() +
                            "]. You must always return a panel with id equal to the provided panelId parameter. TabbedPanel [" +
                            getPath() + "] ITab index [" + currentTab + "]");
        }

        addOrReplace(component);
    }

    private WebMarkupContainer newPanel() {
        return new WebMarkupContainer(TAB_PANEL_ID);
    }

    /**
     * @return index of the selected tab
     */
    public final int getSelectedTab() {
        return (Integer) getDefaultModelObject();
    }

    /**
     * sets the selected tab
     *
     * @param index index of the tab to select
     * @return this for chaining
     * @throws IndexOutOfBoundsException if index is not in the range of available tabs
     */
    public DynamicTabbedPanel<T> setSelectedTab(final int index) {
        if ((index < 0) || (index >= tabs.map(List::size).getObject())) {
            throw new IndexOutOfBoundsException();
        }

        setDefaultModelObject(index);

        // force the tab's component to be aquired again if already the current tab
        currentTab = -1;
        setCurrentTab(index);

        return this;
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        tabs.detach();
    }


}
