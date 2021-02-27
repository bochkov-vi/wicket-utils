package com.bochkov.wicket.jpa.crud.modal;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Accessors(chain = true)
public class ModalDialog<T> extends GenericPanel<T> {

    public static final String BODY_ID = "body";

    @Setter
    @Getter
    Component body = new EmptyPanel(BODY_ID);

    WebMarkupContainer content = new WebMarkupContainer("content");

    @Getter
    List<SerializableFunction<String, Component>> buttons = Lists.newArrayList();

    @Setter
    @Getter
    IModel<String> header;

    String dialogSize = DialogSize.NORMAL;


    public ModalDialog(String id) {
        super(id);
    }

    public ModalDialog(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        content.setOutputMarkupId(true);
        add(content);
        content.add(new Label("header", () -> header));
        content.add(body);
        add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.addAll(Lists.newArrayList("modal-dialog" + dialogSize, "modal-dialog-centered"));
                return oldClasses;
            }
        });
        add(new AttributeAppender("role", "document"));
        add(new ListView<SerializableFunction<String, Component>>("buttons") {
            @Override
            protected void populateItem(ListItem<SerializableFunction<String, Component>> item) {
                item.add(item.getModelObject().apply("button"));
            }
        });
    }

    public ModalDialog<T> setBody(Component component) {
        if (Objects.equals(component.getId(), BODY_ID)) {
            body = component;
            this.replace(body);
        }
        return this;
    }

    public String showScript() {
        return String.format("$(#%s).modal('show')", getMarkupId());
    }

    public String hideScript() {
        return String.format("$(#%s).modal('hide')", getMarkupId());
    }

    public void show(AjaxRequestTarget target, boolean update) {
        target.appendJavaScript(showScript());
        if (update) {
            target.add(content);
        }
    }

    public void hide(AjaxRequestTarget target, boolean update) {
        target.appendJavaScript(hideScript());
        if (update) {
            target.add(content);
        }
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }

    public static class DialogSize {

        public static String NORMAL = "";

        public static String LAGE = "_lg";

        public static String SMALL = "_sm";
    }

}
