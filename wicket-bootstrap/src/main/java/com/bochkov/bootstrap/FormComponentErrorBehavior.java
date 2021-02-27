package com.bochkov.bootstrap;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.Set;
import java.util.stream.Stream;

public class FormComponentErrorBehavior extends Behavior {

    protected FormComponent formComponent;


    public static void appendFormComponent(FormComponent... components) {
        Stream.of(components).forEach(cmp -> {
            if (!isHasFormComponentErrorBehavior(cmp)) {
                cmp.add(new FormComponentErrorBehavior());
            }
        });
    }

    public static boolean isHasFormComponentErrorBehavior(Component cmp) {
        return cmp != null && cmp.getBehaviors().stream().anyMatch(b -> b instanceof FormComponentErrorBehavior);
    }

    public static boolean canRender(FeedbackMessage msg) {
        return msg != null && msg.isError() && isHasFormComponentErrorBehavior(msg.getReporter());
    }

    public static void append(MarkupContainer container) {
        container.visitChildren(FormComponent.class, new IVisitor<FormComponent, Object>() {
            @Override
            public void component(FormComponent formComponent, IVisit<Object> visit) {
                appendFormComponent(formComponent);
            }
        });
    }

    @Override
    public void bind(Component component) {
        formComponent = (FormComponent) component;
        formComponent.setOutputMarkupId(true);
        formComponent.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (formComponent.hasErrorMessage()) {
                    oldClasses.add("is-invalid");
                }
                return oldClasses;
            }
        });
    }


    @Override
    public void onConfigure(Component component) {
        //formComponent.getFeedbackMessages().forEach(FeedbackMessage::markRendered);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        if (formComponent.hasErrorMessage() && component.isVisibleInHierarchy()) {
            for (FeedbackMessage message : formComponent.getFeedbackMessages().messages(msg -> msg.isError() && !msg.isRendered())) {
                message.markRendered();
                response.render(OnDomReadyHeaderItem.forScript(createJavaScript(message)));
            }
        }
    }

    public String createJavaScript(FeedbackMessage message) {
        return String.format("$('#%s').closest('.form-group').append(\"<div class='invalid-feedback d-block'>%s</div>\")",
                formComponent.getMarkupId(), Strings.escapeMarkup(String.valueOf(message.getMessage())));
    }

}
