package com.bochkov.bootstrap;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import java.util.Set;

public class ActiveLinkBehavior extends Behavior {
    @Override
    public void bind(final Component component) {
        super.bind(component);
        if (!(component instanceof BookmarkablePageLink)) {
            throw new IllegalArgumentException(String.format("ActiveLinkBehavior is for BookmarkablePageLink.class not for %s", component.getClass()));
        }
        final BookmarkablePageLink link = (BookmarkablePageLink) component;
        component.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (link.getPageClass().isAssignableFrom(link.getPage().getPageClass())) {
                    oldClasses.add("active");
                }
                return oldClasses;
            }
        });
    }
}
