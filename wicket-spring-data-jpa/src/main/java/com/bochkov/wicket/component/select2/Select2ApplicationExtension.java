package com.bochkov.wicket.component.select2;

import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.protocol.http.WebApplication;
import org.wicketstuff.select2.ApplicationSettings;

import java.util.List;

/*@ApplicationInitExtension*/
public class Select2ApplicationExtension /*implements WicketApplicationInitConfiguration */{

    /*@Override*/
    public void init(WebApplication webApplication) {
        ApplicationSettings.get().setJavaScriptReference(new WebjarsJavaScriptResourceReference("resources/webjars/select2/current/js/select2.full.js"));
        ApplicationSettings.get().setCssReference(new WebjarsCssResourceReference("resources/webjars/select2/current/css/select2.css"));
        ApplicationSettings.get().setCssReference(new WebjarsCssResourceReference("resources/webjars/select2-bootstrap4-theme/current/dist/select2-bootstrap4.min.css") {
            @Override
            public List<HeaderItem> getDependencies() {
                return Lists.newArrayList(CssHeaderItem.forReference(new WebjarsCssResourceReference("resources/webjars/select2/current/css/select2.css")));
            }
        });
    }
}
