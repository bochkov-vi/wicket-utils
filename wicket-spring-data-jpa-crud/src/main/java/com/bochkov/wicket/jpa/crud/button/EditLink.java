package com.bochkov.wicket.jpa.crud.button;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Set;

public abstract class EditLink<T> extends Link<T> {

    public EditLink(String id) {
        super(id);
    }

    public EditLink(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setEscapeModelStrings(true);
        setBody(Model.of("<span class='fa fa-pencil'></span>"));
        add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("btn");
                oldClasses.add("btn-outline-primary");
                return oldClasses;
            }
        });
    }
}
