package com.bochkov.fontawesome;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.ResourceReference;

public class FontAwesomeBehavior extends Behavior {

    public static ResourceReference CSS = new WebjarsCssResourceReference("font-awesome/current/css/font-awesome.min.css");



    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(CssHeaderItem.forReference(CSS));
    }
}
