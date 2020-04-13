package com.bochkov.bootstrap.modal;

import com.github.openjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.IModel;

import java.util.Set;

@Accessors(chain = true)
@Getter
public class BootstrapModalPanel extends Panel {

    public static final String BODY_CONTENT_ID = "modal-body";

    MarkupContainer modalDialog = new WebMarkupContainer("modal-dialog");


    MarkupContainer footer = createFooter("modal-footer");

    Label headerLabel = new Label("header-label");

    @Getter
    @Setter
    private Boolean show = false;

    @Getter
    @Setter
    private Boolean fade = true;

    @Getter
    @Setter
    private Boolean focus = true;

    @Getter
    @Setter
    private Boolean keyboard = true;

    @Getter
    @Setter
    private Backdrop backdrop = Backdrop.TRUE;

    @Getter
    @Setter
    private Size size = Size.Default;

    public BootstrapModalPanel(String id) {
        super(id);
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        add(modalDialog);
        modalDialog.add(new EmptyPanel(BODY_CONTENT_ID), footer, headerLabel);
        add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("modal");
                if (fade)
                    oldClasses.add("fade");
                return oldClasses;
            }
        });
        modalDialog.add(new AttributeAppender("class", () -> size.cssClassName(), " "));
        headerLabel.setOutputMarkupId(true);
        add(new AttributeModifier("data-backdrop", ((IModel<Backdrop>) () -> backdrop).filter(b -> b != Backdrop.TRUE).map(Backdrop::name).map(String::toLowerCase)));
        add(new AttributeAppender("role", "dialog"));
        add(new AttributeAppender("aria-labelledby", headerLabel.getMarkupId()));
        add(new AttributeAppender("aria-hidden", "true"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript(jsCreate()));
    }

    public BootstrapModalPanel setBody(Component component) {
        if (component.getId().equals(BODY_CONTENT_ID) == false) {
            throw new WicketRuntimeException("Modal window content id is wrong. Component ID:" +
                    component.getId() + "; content ID: " + BODY_CONTENT_ID);
        } else if (component instanceof AbstractRepeater) {
            throw new WicketRuntimeException(
                    "A repeater component cannot be used as the content of a modal window, please use repeater's parent");
        }

        component.setOutputMarkupPlaceholderTag(true);
        component.setVisible(false);
        replace(component);
        show = false;
        return this;
    }


    MarkupContainer createFooter(String id) {
        WebMarkupContainer footer = new WebMarkupContainer(id);
        return footer;
    }

    String toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("backdrop", backdrop);
        jsonObject.put("keyboard", keyboard);
        jsonObject.put("focus", focus);
        jsonObject.put("show", show);
        return jsonObject.toString();
    }

    public void show(AjaxRequestTarget target) {
        target.appendJavaScript(jsShow());
    }

    public void show(AjaxRequestTarget target, Component component) {
        setBody(component);
        show(target);
        resize(target);
    }

    public void hide(AjaxRequestTarget target) {
        target.appendJavaScript(jsHide());
    }

    public void resize(AjaxRequestTarget target) {
        target.appendJavaScript(jsResize());
    }

    public String jsCreate() {
        return String.format("$('#%s').modal(%s)", getMarkupId(), toJson());
    }

    public String jsShow() {
        return String.format("$('#%s').modal('show')", getMarkupId(), toJson());
    }

    public String jsResize() {
        return String.format("$('#%s').modal('handleUpdate')", getMarkupId(), toJson());
    }

    public String jsHide() {
        return String.format("$('#%s').modal('hide')", getMarkupId(), toJson());
    }

    public enum Backdrop {
        TRUE, FALSE, STATIC
    }

    public enum Size {
        Default(""),
        Small("sm"),
        ExtraLarge("xl"),
        Large("lg");

        private final String cssClassName;

        /**
         * Construct.
         *
         * @param cssClassName the css class name of button type
         */
        Size(final String cssClassName) {
            this.cssClassName = cssClassName;
        }

        /**
         * @return css class name of button type
         */
        public String cssClassName() {
            return "modal-" + cssClassName;
        }
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "div");
    }

}
