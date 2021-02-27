package com.bochkov.wicket.jpa.crud;

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;

public abstract class CompositeInputPanel<T> extends FormComponentPanel<T> {

    public CompositeInputPanel(String id) {
        super(id);
    }

    public CompositeInputPanel(String id, IModel<T> model) {
        super(id, model);
    }

    public boolean formHasError() {
        return getForm().hasError();
    }

    abstract protected void initBeforeRenderer();

    @Override
    protected final void onBeforeRender() {
        if (!formHasError()) {
            initBeforeRenderer();
        }
        super.onBeforeRender();
    }
}
