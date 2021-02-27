package com.bochkov.wicket.component;

import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import lombok.Getter;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;

import java.util.List;

public class InputMaskBehavior extends Behavior {

    FormComponent formComponent;

    @Getter
    String mask;

    public InputMaskBehavior(String mask) {
        this.mask = mask;
    }

    public static InputMaskBehavior phone() {
        return new InputMaskBehavior("+7(999) 999-99-99");
    }

    public static InputMaskBehavior email() {
        return new InputMaskBehavior("email");
    }

    @Override
    public void bind(Component component) {
        super.bind(component);
        formComponent = (FormComponent) component;
        formComponent.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("jquery.inputmask/current/jquery.inputmask.bundle.js") {
            @Override
            public List<HeaderItem> getDependencies() {
                return Lists.newArrayList(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
            }
        }));

        response.render(OnDomReadyHeaderItem.forScript(jsCreate()));
    }

    public String jsCreate() {
        return String.format("$('#%s').inputmask('%s')", formComponent.getMarkupId(), mask);
    }

}
