package com.bochkov.wicket.jpa.crud.duplicate;

import lombok.Getter;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;

public abstract class FeedbackMessageComponentOnDuplicateEntity<T> extends FeedbackMessageComponent {

    @Getter
    IModel<T> model;


    public FeedbackMessageComponentOnDuplicateEntity(Component reporter, Serializable message, IModel<T> model) {
        super(reporter, message, WARNING);
        this.model = model;
    }

    public Component createDisplayComponent(String id) {
        return new DuplicateEntityMessageLink<T>(id, model, Model.of((String) getMessage())) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                FeedbackMessageComponentOnDuplicateEntity.this.onClick(target);
            }
        };
    }

    public void onClick(AjaxRequestTarget target) {

    }
}
