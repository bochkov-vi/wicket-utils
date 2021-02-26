package com.bochkov.wicket.select2;

import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.wicketstuff.select2.ApplicationSettings;

import java.util.List;

/**
 * The type Select 2 application extension.
 */
/*@ApplicationInitExtension*/
public class Select2ApplicationExtension /*implements WicketApplicationInitConfiguration */ {

    public static void install(WebApplication webApplication) {
        ApplicationSettings.get().setJavaScriptReference(new WebjarsJavaScriptResourceReference("resources/webjars/select2/current/js/select2.full.js"));
        //ApplicationSettings.get().setCssReference(new WebjarsCssResourceReference("resources/webjars/select2/current/css/select2.css"));
        ApplicationSettings.get().setCssReference(new PackageResourceReference(Select2ApplicationExtension.class, "select2-bootstrap4.min.css") {
            @Override
            public List<HeaderItem> getDependencies() {
                return Lists.newArrayList(CssHeaderItem.forReference(new WebjarsCssResourceReference("resources/webjars/select2/current/css/select2.css")));
            }
        });
    }

    /**
     * Init.
     *
     * @param webApplication the web application
     */
    /*@Override*/
    public void init(WebApplication webApplication) {
        install(webApplication);
    }
}
