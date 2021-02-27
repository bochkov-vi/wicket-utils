package com.bochkov.wicket.jpa.crud.modal;

import org.apache.wicket.model.IModel;

public class DeleteDialog<T> extends ModalDialog<T> {

    public DeleteDialog(String id) {
        super(id);
    }

    public DeleteDialog(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
}
