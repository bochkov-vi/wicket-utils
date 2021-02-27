package com.bochkov.wicket.jpa.crud;

import com.bochkov.wicket.component.table.XLSXDataExportLink;
import com.bochkov.wicket.data.model.nonser.CollectionModel;
import com.bochkov.wicket.data.provider.PersistableDataProvider;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;

public abstract class CrudTablePanel<T extends Persistable<ID>, ID extends Serializable> extends CrudPanel<Collection<T>, T, ID> {


    WebMarkupContainer container = new WebMarkupContainer("container");


    EntityDataTable<T, ID> table = null;

    ScrollToAnchorBehavior<T> scrollToAnchorBehavior;

    boolean ajax = false;

    XLSXDataExportLink exportExcel;

    @Getter
    private IModel<String> exportFileName;

    public CrudTablePanel(String id, Class<T> tClass) {
        super(id, tClass);
    }

    public CrudTablePanel(String id, Class<T> tClass, IModel<Collection<T>> model) {
        super(id, tClass, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (getModel() == null) {
            setModel(CollectionModel.of(id -> getRepository().findById(id)));
        }
        scrollToAnchorBehavior = new ScrollToAnchorBehavior(entityClass);
        exportFileName = new ResourceModel("exportFileName").wrapOnAssignment(this);
        table = new EntityDataTable<T, ID>("table", columns(), provider()) {

            @Override
            public void onRowCreated(Item<T> row, String id, int index, IModel<T> model) {
                CrudTablePanel.this.onRowCreated(table, row, id, index, model);
                /*row.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {
                        if (model.combineWith(CrudTablePage.this.getModel(), (e, collection) -> collection.contains(e)).getObject()) {
                            oldClasses.addAll(Lists.newArrayList("border-success","border"));
                        }

                        return oldClasses;
                    }
                });*/
                row.add(new StyleAttributeModifier() {
                    @Override
                    protected Map<String, String> update(Map<String, String> oldStyles) {
                        if (model.combineWith(CrudTablePanel.this.getModel(), (e, collection) -> collection.contains(e)).getObject()) {
                            oldStyles.put("box-shadow", "0 0 30px #44f");
                        }
                        return oldStyles;
                    }
                });
                row.add(scrollToAnchorBehavior.nameAttributeModifier(model));
            }
        };
        exportExcel = new XLSXDataExportLink("export-excel", table, exportFileName.getObject());
        table.setOutputMarkupId(true);
        container.add(table);
        container.setOutputMarkupId(true);
        container.add(createAddRowButton("btn-add-row"));
        container.add(exportExcel);
        add(scrollToAnchorBehavior);
        add(container);
    }


    private ISortableDataProvider<T, String> provider() {
        return PersistableDataProvider.of(this::getRepository, this::specification, this::sort);
    }


    protected List<? extends IColumn<T, String>> columns() {
        List<? extends IColumn<T, String>> result = Lists.newArrayList();
        return result;
    }

    protected Specification<T> specification() {
        return null;
    }

    protected Sort sort() {
        return null;
    }

    public Component createEditButton(String id, IModel<T> model) {
        AbstractLink button = null;
        if (ajax) {
            button = createEditAjaxButton(id, model);
        } else {
            button = createEditSimpleButton(id, model);
        }
        button.setEscapeModelStrings(false);

        button.setBody(Model.of("<span class='fa fa-pencil'></span>"));
        button.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("btn");
                oldClasses.add("btn-outline-info");
                return oldClasses;
            }
        });
        return button;
    }


    public IColumn<T, String> createEditColumn() {
        return new HeaderlessColumn<T, String>() {
            @Override
            public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
                cellItem.add(createEditButton(componentId, rowModel));
            }
        };
    }

    public IColumn<T, String> createDeleteColumn() {
        return new HeaderlessColumn<T, String>() {
            @Override
            public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
                cellItem.add(createDeleteButton(componentId, rowModel));
            }
        };
    }

    public Component createAddRowButton(String id) {
        if (ajax) {
            return createAddRowAjaxButton(id);
        } else {
            return createAddRowSimpleButton(id);
        }
    }


    public Component createAddRowAjaxButton(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAddRow(Optional.of(target));
            }
        };
    }

    public Component createAddRowSimpleButton(String id) {
        return new Link<Void>(id) {
            @Override
            public void onClick() {
                onAddRow(Optional.empty());
            }
        };
    }


    public AbstractLink createEditAjaxButton(String id, IModel<T> model) {
        return new AjaxLink<T>(id, model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onEdit(Optional.of(target), model);
            }
        };
    }

    public AbstractLink createEditSimpleButton(String id, IModel<T> model) {
        return new Link<T>(id, model) {
            @Override
            public void onClick() {
                onEdit(Optional.empty(), model);
            }
        };
    }

    public void onEdit(Optional<AjaxRequestTarget> target, IModel<T> model) {
        Page page = createEditPage(model);
        setModelObject(Lists.newArrayList(model.getObject()));
        setResponsePage(page);
    }


    public void onAddRow(Optional<AjaxRequestTarget> target) {
        setResponsePage(setBackPageMeta(getPage(), createEditPage()));
    }

    public abstract Class<? extends Page> getEditPageClass();

    protected Page createEditPage(IModel<T> model) {

        Class<? extends Page> clazz = getEditPageClass();
        Page editPage = null;
        try {
            PageParameters pageParameters = pageParametersForModel(model);
            Constructor<? extends Page> constructor = null;
            constructor = clazz.getConstructor(PageParameters.class);
            editPage = BeanUtils.instantiateClass(constructor, pageParameters);
        } catch (NoSuchMethodException e) {
            editPage = createEditPage();
            editPage.setDefaultModel(model);
        }
        setBackPageMeta(getPage(), editPage);
        scrollToAnchorBehavior.setAnchor(model);
        return editPage;
    }

    private Page createEditPage() {
        Page page = BeanUtils.instantiateClass(getEditPageClass());
        return page;
    }

    PageParameters pageParametersForModel(IModel<T> model) {
        PageParameters parameters = new PageParameters();
        String value = getConverter(getEntityClass()).convertToString(model.getObject(), Session.get().getLocale());
        parameters.set(0, value);
        return parameters;
    }

    @Override
    public void onDelete(AjaxRequestTarget target, IModel<T> model) {
        super.onDelete(target, model);
        target.add(table);
    }

    public void onRowCreated(EntityDataTable<T, ID> table, Item<T> item, final String id, final int index, final IModel<T> model) {

    }
}
