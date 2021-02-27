package com.bochkov.wicket.jpa.crud.duplicate;

import lombok.Getter;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class DuplicateError<E> extends FeedbackMessage {
    @Getter
    IModel<E> model;

    public DuplicateError(Component reporter, Serializable message, IModel<E> model) {
        super(reporter, message, FeedbackMessage.ERROR);
        this.model = model;
    }

    @Override
    public void detach() {
        super.detach();
        model.detach();
    }
}
