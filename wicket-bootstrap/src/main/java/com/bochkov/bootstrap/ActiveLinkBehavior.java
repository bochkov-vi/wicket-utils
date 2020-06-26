package com.bochkov.bootstrap;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Set;

/**
 * The type Active link behavior.
 *
 * @param <C> the type parameter
 */
public abstract class ActiveLinkBehavior<C extends Component> extends Behavior {

    /**
     * For bookmarkable active link behavior.
     *
     * @return the active link behavior
     */
    public static ActiveLinkBehavior<BookmarkablePageLink> forBookmarkable() {
        return new ActiveLinkBehavior<BookmarkablePageLink>() {
            @Override
            public boolean isActive(BookmarkablePageLink link) {
                return link.getPageClass().isAssignableFrom(link.getPage().getPageClass());
            }
        };
    }

    /**
     * Of active link behavior.
     *
     * @param link the link
     * @return the active link behavior
     */
    public static ActiveLinkBehavior<BookmarkablePageLink> of(BookmarkablePageLink link) {
        return new ActiveLinkBehavior<BookmarkablePageLink>() {
            @Override
            public boolean isActive(BookmarkablePageLink link) {
                return link.getPageClass().isAssignableFrom(link.getPage().getPageClass());
            }
        };
    }


    public static ActiveLinkBehavior<BookmarkablePageLink> of() {
        return new ActiveLinkBehavior<BookmarkablePageLink>() {
            @Override
            public boolean isActive(BookmarkablePageLink link) {
                return link.getPageClass().isAssignableFrom(link.getPage().getPageClass());
            }
        };
    }

    /**
     * Of active link behavior.
     *
     * @param <C>       the type parameter
     * @param component the component
     * @param predicate the predicate
     * @return the active link behavior
     */
    public static <C extends Component> ActiveLinkBehavior<C> of(C component, final SerializableFunction<C, Boolean> predicate) {
        return new ActiveLinkBehavior<C>() {
            public boolean isActive(C component) {
                return predicate.apply(component);
            }
        };
    }

    /**
     * Of active link behavior.
     *
     * @param <C>       the type parameter
     * @param predicate the predicate
     * @return the active link behavior
     */
    public static <C extends Component> ActiveLinkBehavior<C> of(final SerializableFunction<C, Boolean> predicate) {
        return new ActiveLinkBehavior<C>() {
            public boolean isActive(C component) {
                return predicate.apply(component);
            }
        };
    }


    public static <C extends Component> ActiveLinkBehavior<C> of(Class<? extends Page> pageClass) {
        return new ActiveLinkBehavior<C>() {
            @Override
            public boolean isActive(C component) {
                return component.getPage().getClass().isAssignableFrom(pageClass);
            }
        };
    }

    @Override
    public void bind(final Component component) {
        super.bind(component);
        component.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (isActive((C) component)) {
                    oldClasses.add("active");
                } else {
                    oldClasses.remove("active");
                }
                return oldClasses;
            }
        });
    }

    /**
     * Is active boolean.
     *
     * @param component the component
     * @return the boolean
     */
    public abstract boolean isActive(C component);
}
