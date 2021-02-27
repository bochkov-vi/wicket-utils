package com.bochkov.wicket.jpa.crud;

import com.bochkov.wicket.component.table.XLSXDataExportLink;
import com.bochkov.wicket.data.model.nonser.CollectionModel;
import com.bochkov.wicket.data.provider.PersistableDataProvider;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.StyleAttributeModifier;
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

public abstract class CrudTablePage<T extends Persistable<ID>, ID extends Serializable> extends CrudPage<Collection<T>, T, ID> {


    WebMarkupContainer container = new WebMarkupContainer("container");


    EntityDataTable<T, ID> table = null;

    ScrollToAnchorBehavior<T> scrollToAnchorBehavior;

    boolean ajax = false;

    XLSXDataExportLink exportExcel;

    @Getter
    private IModel<String> exportFileName;

    public CrudTablePage(Class<T> tClass) {
        super(tClass);
    }

    public CrudTablePage(Class<T> tClass, IModel<Collection<T>> model) {
        super(tClass, model);
    }

    public CrudTablePage(Class<T> tClass, PageParameters parameters) {
        super(tClass, parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (getModel() == null) {
            setModel(CollectionModel.of(id -> getRepository().findById(id)));
        }
        scrollToAnchorBehavior = new ScrollToAnchorBehavior(entityClass);
        exportFileName = new ResourceModel("exportFileName").wrapOnAssignment(getPage());
        table = new EntityDataTable<T, ID>("table", columns(), provider()) {

            @Override
            public void onRowCreated(Item<T> row, String id, int index, IModel<T> model) {
                CrudTablePage.this.onRowCreated(table, row, id, index, model);
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
                        if (model.combineWith(CrudTablePage.this.getModel(), (e, collection) -> collection.contains(e)).getObject()) {
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
        CrudEditPage<T, ID> page = createEditPage(model);
        setModelObject(Lists.newArrayList(model.getObject()));
        setResponsePage(page);
    }


    public void onAddRow(Optional<AjaxRequestTarget> target) {
        setResponsePage(createEditPage().setBackPage(this));
    }

    public abstract Class<? extends CrudEditPage<T, ID>> getEditPageClass();

    final private CrudEditPage<T, ID> createEditPage(IModel<T> model) {

        Class<? extends CrudEditPage<T, ID>> clazz = getEditPageClass();
        CrudEditPage<T, ID> page = null;
        try {
            PageParameters pageParameters = pageParametersForModel(model);
            Constructor<? extends CrudEditPage<T, ID>> constructor = null;
            constructor = clazz.getConstructor(PageParameters.class);
            page = BeanUtils.instantiateClass(constructor, pageParameters);
        } catch (NoSuchMethodException e) {
            page = createEditPage();
            page.setModel(model);
        }
        page.setBackPage(this);
        scrollToAnchorBehavior.setAnchor(model);
        return page;
    }

    private CrudEditPage<T, ID> createEditPage() {
        CrudEditPage<T, ID> page = BeanUtils.instantiateClass(getEditPageClass());
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
