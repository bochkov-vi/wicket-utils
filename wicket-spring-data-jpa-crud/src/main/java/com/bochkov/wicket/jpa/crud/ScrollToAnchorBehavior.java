package com.bochkov.wicket.jpa.crud;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.Optional;

@Accessors(chain = true)
public class ScrollToAnchorBehavior<T> extends Behavior {

    public static String ANCHOR_PARAM = "anchor";

    @Getter
    @Setter
    String anchor;

    Component component;

    private Class<T> entityClass;

    public ScrollToAnchorBehavior(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void bind(Component component) {
        super.bind(component);
        this.component = component;
    }

    @Override
    public void renderHead(Component comp, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ScrollToAnchorBehavior.class, "ScrollToAnchor.js")));
        if (!Strings.isNullOrEmpty(anchor)) {
            response.render(new OnLoadHeaderItem(String.format("scrollToAnchor('%s')", anchor)));
        }
        anchor = null;
    }

    @Override
    public void onConfigure(Component component) {
        if (!component.getPage().getPageParameters().get(ANCHOR_PARAM).isEmpty()) {
            anchor = component.getPage().getPageParameters().get(ANCHOR_PARAM).toOptionalString();
        }
        super.onConfigure(component);
    }


    public String createAnchor(IModel<T> model) {
        return model.map(this::createAnchor).getObject();
    }

    public String createAnchor(T entity) {
        return Optional.ofNullable(entity).map(e->component.getConverter(entityClass).convertToString(e, Session.get().getLocale())).orElse(null);
    }

    public AttributeModifier nameAttributeModifier(IModel<T> model) {
        return new AttributeModifier("name", createAnchor(model));
    }

    public ScrollToAnchorBehavior<T> setAnchor(IModel<T> model) {
        this.anchor = createAnchor(model);
        return this;
    }

    public ScrollToAnchorBehavior<T> setAnchor(T entity) {
        this.anchor = createAnchor(entity);
        return this;
    }
}
