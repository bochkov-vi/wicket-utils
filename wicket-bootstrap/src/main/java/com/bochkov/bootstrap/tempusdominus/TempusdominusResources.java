package com.bochkov.bootstrap.tempusdominus;

import com.bochkov.bootstrap.BootstrapBehavior;
import com.bochkov.fontawesome.FontAwesomeBehavior;
import com.google.common.collect.ImmutableList;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

import java.util.List;

public class TempusdominusResources extends FontAwesomeBehavior {
    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("tempusdominus-bootstrap-4/current/js/tempusdominus-bootstrap-4.js") {
            @Override
            public List<HeaderItem> getDependencies() {
                return ImmutableList.of(
                        JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()),
                        JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("momentjs/current/min/moment-with-locales.min.js")),
                        JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("moment-jdateformatparser/current/moment-jdateformatparser.min.js")));
            }
        }));
        response.render(CssHeaderItem.forReference(new WebjarsCssResourceReference("tempusdominus-bootstrap-4/current/css/tempusdominus-bootstrap-4.min.css") {
            @Override
            public List<HeaderItem> getDependencies() {
                return ImmutableList.of(CssHeaderItem.forReference(BootstrapBehavior.CSS));
            }
        }));
    }
}
