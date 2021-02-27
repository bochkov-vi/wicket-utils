package com.bochkov.wicket.jpa.crud.duplicate;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class DuplicateEntityMessageLink<T> extends GenericPanel<T> {

    IModel<String> labelModel = Model.of("Обнаружен дубликат объекта, перейти к редактированию");

    public DuplicateEntityMessageLink(String id, IModel<T> model, IModel<String> label) {
        super(id, model);
        this.labelModel = label;
    }

    public DuplicateEntityMessageLink(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AjaxLink<T> ajaxLink = new AjaxLink<T>("link", getModel()) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                DuplicateEntityMessageLink.this.onClick(target);
            }
        };

        add(ajaxLink);
        add(new Label("label", labelModel));
    }

    public void onClick(AjaxRequestTarget target) {

    }
}
