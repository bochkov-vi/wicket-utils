package com.bochkov.wicket.jpa.crud;

import com.bochkov.wicket.jpa.model.PersistableModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Set;

@Accessors(chain = true)
public class DeletePanel<T extends Persistable<ID>, ID extends Serializable> extends Border {

    @Getter
    @Setter
    IModel<T> deletedEntityModel;

    WebMarkupContainer container = new WebMarkupContainer("container");

    public DeletePanel(String id) {
        super(id);
    }

    public DeletePanel(String id, PersistableModel<T, ID> model) {
        super(id);
        deletedEntityModel = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addToBorder(container);
        add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("modal");
                oldClasses.add("fade");
                return oldClasses;
            }
        });
        add(new AttributeAppender("tabindex", -1));
        add(new AttributeAppender("role", "dialog"));
        container.add(new Label("header", getDefaultModel()));
        Form form = new Form("form");
        container.add(form);
        Button btnDelete = new AjaxButton("btn-delete", form) {
            @Override
            public void onSubmit(AjaxRequestTarget target) {
                onDelete(target, deletedEntityModel);
            }
        };
        container.setOutputMarkupId(true);
        container.add(btnDelete);
        setOutputMarkupId(true);
    }

    public void onDelete(AjaxRequestTarget target, IModel<T> model) {

    }

    public void show(AjaxRequestTarget target) {
        target.add(container);
        target.appendJavaScript(String.format("$('#%s').modal('show')", getMarkupId()));
    }

    public void hide(AjaxRequestTarget target) {
        deletedEntityModel.setObject(null);
        target.appendJavaScript(String.format("$('#%s').modal('hide')", getMarkupId()));
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        deletedEntityModel.detach();
    }
}
