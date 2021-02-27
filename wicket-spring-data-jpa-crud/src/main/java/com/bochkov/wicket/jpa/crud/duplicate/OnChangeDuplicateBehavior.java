package com.bochkov.wicket.jpa.crud.duplicate;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;

public abstract class OnChangeDuplicateBehavior<S, E> extends DuplicateEntityBehavior<S, E> {

    public OnChangeDuplicateBehavior(IModel<E> entityModel, Class<E> entityClass) {
        super(entityModel, entityClass);
    }

    @Override
    protected void onBind() {
        super.onBind();
        formComponent.setOutputMarkupId(true);
        getComponent().add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }

            @Override
            protected void onError(AjaxRequestTarget target, RuntimeException e) {
                target.add(getComponent());
            }
        });
    }

}
