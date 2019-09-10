package com.bochkov.wicket.component;

import com.github.openjson.JSONStringer;
import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.Application;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import org.wicketstuff.select2.json.Json;

import java.time.LocalDate;
import java.util.List;

@Getter
@Accessors(chain = true)
public class LocalDateTextField extends org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField {

    Options options = new Options().setLanguage("ru").setPattern("dd.mm.yyyy");

    public LocalDateTextField(String id, String pattern) {
        super(id, pattern);
    }

    public LocalDateTextField(String id, IModel<LocalDate> model, String pattern) {
        super(id, model, pattern);
    }

    private void init() {

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').datepicker(%s)", getMarkupId(), options)));
        response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("resources/webjars/bootstrap-datepicker/current/js/bootstrap-datepicker.js") {
            @Override
            public List<HeaderItem> getDependencies() {
                return Lists.newArrayList(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
            }
        }));
        response.render(CssHeaderItem.forReference(new WebjarsCssResourceReference("resources/webjars/bootstrap-datepicker/current/css/bootstrap-datepicker.css")));
        response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("resources/webjars/bootstrap-datepicker/current/locales/bootstrap-datepicker.ru.min.js")));
    }


    @Getter
    @Setter
    @Accessors(chain = true)
    static class Options implements IClusterable {

        String language;

        String pattern;

        Boolean clearBtn = true;

        Boolean todayBtn = true;

        Boolean todayHighlight = true;

        @Override
        public String toString() {
            JSONStringer stringer = new JSONStringer();
            stringer.object();
            Json.writeObject(stringer, "language", language);
            Json.writeObject(stringer, "pattern", pattern);
            Json.writeObject(stringer, "clearBtn", clearBtn);
            Json.writeObject(stringer, "todayBtn", todayBtn);
            Json.writeObject(stringer, "todayHighlight", todayHighlight);
            stringer.endObject();
            return stringer.toString();
        }
    }
}
