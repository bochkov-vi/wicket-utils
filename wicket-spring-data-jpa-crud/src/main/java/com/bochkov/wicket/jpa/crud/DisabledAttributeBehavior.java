package com.bochkov.wicket.jpa.crud;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

import java.util.Set;

public class DisabledAttributeBehavior extends Behavior {

    @Override
    public void bind(Component component) {
        super.bind(component);
        component.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (!component.isEnabledInHierarchy()) {
                    oldClasses.add("disabled");
                }
                return oldClasses;
            }
        });
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if (!component.isEnabledInHierarchy()) {
            tag.put("disabled", "disabled");
        }
    }
}
