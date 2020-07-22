package com.bochkov.bootstrap;

import com.google.common.collect.ImmutableList;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.ResourceReference;

import java.util.List;

/**
 * The type Bootstrap behavior.
 */
public class BootstrapBehavior extends BootstrapCssBehavior {

    public static ResourceReference JS = new WebjarsCssResourceReference("bootstrap/current/js/bootstrap.min.js") {
        @Override
        public List<HeaderItem> getDependencies() {
            return ImmutableList.of(
                    JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()),
                    JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("popper.js/current/umd/popper.min.js"))
            );
        }
    };

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(BootstrapBehavior.JS));
    }
}
