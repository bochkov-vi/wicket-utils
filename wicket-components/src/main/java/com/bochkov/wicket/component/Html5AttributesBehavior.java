package com.bochkov.wicket.component;

import com.google.common.base.Strings;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.HTML5Attributes;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidatorAdapter;
import org.apache.wicket.validation.validator.PatternValidator;

import java.util.Optional;
import java.util.stream.Stream;

public class Html5AttributesBehavior extends HTML5Attributes {


    private static final long serialVersionUID = 1L;

    public static void appendFormComponent(FormComponent... components) {
        Stream.of(components).forEach(cmp -> {
            if (cmp.getBehaviors().stream().noneMatch(b -> b instanceof Html5AttributesBehavior)) {
                cmp.add(new Html5AttributesBehavior());
            }
        });
    }

    public static void append(MarkupContainer form) {
        form.visitChildren(FormComponent.class, new IVisitor<FormComponent, Object>() {
            @Override
            public void component(FormComponent cmp, IVisit<Object> visit) {
                appendFormComponent(cmp);
            }
        });
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if (component instanceof AbstractTextComponent) {
            onInput((AbstractTextComponent<?>) component, tag);
        } else if (component instanceof Button) {
            onButton((Button) component, tag);
        }
    }

    protected void onInput(AbstractTextComponent<?> input, ComponentTag tag) {
        if (input.isRequired()) {
            tag.put("required", "required");
        }
        if (Strings.isNullOrEmpty(tag.getAttribute("placeholder"))) {
            String label = Optional.ofNullable(input.getLabel()).map(IModel::getObject).orElse(null);
            if (!Strings.isNullOrEmpty(label)) {
                tag.put("placeholder", label);
            }
            if (Strings.isNullOrEmpty(label)) {
                label = input.getDefaultLabel();
                if (!Strings.isNullOrEmpty(label)) {
                    tag.put("placeholder", label);
                }
            }
        }
        for (IValidator<?> validator : input.getValidators()) {
            while (validator instanceof ValidatorAdapter) {
                validator = ((ValidatorAdapter<?>) validator).getValidator();
            }

            if (validator instanceof PatternValidator) {
                tag.put("pattern", ((PatternValidator) validator).getPattern().toString());
            }
        }
    }


    protected void onButton(Button button, ComponentTag tag) {
        if (!button.getDefaultFormProcessing()) {
            tag.put("formnovalidate", "formnovalidate");
        }
    }


    public static class InstantiationListener implements IComponentInstantiationListener {

        /**
         * Adds {@link HTML5Attributes} to all {@link FormComponent}s.
         */
        @Override
        public void onInstantiation(Component component) {
            if (component instanceof FormComponent) {
                appendFormComponent((FormComponent) component);
            }
        }
    }


}
