package com.bochkov.bootstrap;

import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

import java.util.List;

/**
 * The type Bootstrap behavior.
 */
public class BootstrapBehavior extends Behavior {
    @Override
    public void bind(Component component) {
        super.bind(component);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("webjars/bootstrap/current/js/bootstrap.js") {
            @Override
            public List<HeaderItem> getDependencies() {
                List<HeaderItem> result = Lists.newArrayList();
                result.add(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
                result.add(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("webjars/popper.js/current/umd/popper.min.js")));
                return result;

            }
        }));
        response.render(CssHeaderItem.forReference(new WebjarsCssResourceReference("webjars/bootstrap/current/css/bootstrap.css")));
        response.render(CssHeaderItem.forReference(new WebjarsCssResourceReference("resources/webjars/font-awesome/current/css/font-awesome.css")));
    }
}
