package com.bochkov.bootstrap;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Set;

public abstract class ActiveLinkBehavior<C extends Component> extends Behavior {

    public static ActiveLinkBehavior<BookmarkablePageLink> forBookmarkable() {
        return new ActiveLinkBehavior<BookmarkablePageLink>() {
            @Override
            public boolean isActive(BookmarkablePageLink link) {
                return link.getPageClass().isAssignableFrom(link.getPage().getPageClass());
            }
        };
    }

    public static ActiveLinkBehavior<BookmarkablePageLink> of(BookmarkablePageLink link) {
        return new ActiveLinkBehavior<BookmarkablePageLink>() {
            @Override
            public boolean isActive(BookmarkablePageLink link) {
                return link.getPageClass().isAssignableFrom(link.getPage().getPageClass());
            }
        };
    }


    public static <C extends Component> ActiveLinkBehavior<C> of(C component, final SerializableFunction<C, Boolean> predicate) {
        return new ActiveLinkBehavior<C>() {
            public boolean isActive(C component) {
                return predicate.apply(component);
            }
        };
    }

    public static <C extends Component> ActiveLinkBehavior<C> of(final SerializableFunction<C, Boolean> predicate) {
        return new ActiveLinkBehavior<C>() {
            public boolean isActive(C component) {
                return predicate.apply(component);
            }
        };
    }


    @Override
    public void bind(final Component component) {
        super.bind(component);
        component.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (isActive((C) component))
                    oldClasses.add("active");
                return oldClasses;
            }
        });
    }

    public abstract boolean isActive(C component);
}
