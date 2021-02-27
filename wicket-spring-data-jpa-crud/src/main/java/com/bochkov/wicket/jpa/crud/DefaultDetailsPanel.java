package com.bochkov.wicket.jpa.crud;

import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.springframework.beans.BeanUtils;

import java.beans.FeatureDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;


public class DefaultDetailsPanel<T> extends GenericPanel<T> {

    Class<T> entityClass;

    public DefaultDetailsPanel(String id, IModel<T> model) {
        super(id, model);
        entityClass = (Class<T>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
    }

    public DefaultDetailsPanel(String id, Class<T> entityClass) {
        super(id);
        this.entityClass = entityClass;
    }

    public DefaultDetailsPanel(String id, IModel<T> model, Class<T> entityClass) {
        super(id, model);
        this.entityClass = entityClass;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        List<String> properties = Lists.newArrayList(BeanUtils.getPropertyDescriptors(entityClass)).stream()
                .filter(pd -> pd.getReadMethod() != null)
                .map(FeatureDescriptor::getName)
                .filter(name -> !"class".equalsIgnoreCase(name))

                .collect(Collectors.toList());
        ListView list = new ListView<String>("list", properties) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Label("name", new ResourceModel(item.getModelObject(), item.getModelObject())));
                item.add(new Label("value", new PropertyModel<>(DefaultDetailsPanel.this.getModel(), item.getModelObject())));
            }
        };
        add(list);
    }
}
