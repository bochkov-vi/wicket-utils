package com.bochkov.bootstrap.tempusdominus;

import com.github.openjson.JSONObject;
import com.google.common.collect.ImmutableList;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.*;
import org.apache.wicket.ajax.json.JSONFunction;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.util.io.IClusterable;

import java.io.Serializable;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class TempusdominusBehavior extends Behavior implements ITempusdominus {

    String format;

    String locale;

    Buttons buttons = new Buttons();

    Boolean useStrict = true;

    Boolean dateOnly;

    Boolean useCurrent;

    Temporal minDate;

    Temporal maxDate;

    AbstractTextComponent textComponent;

    String tempusdominusId;

    public TempusdominusBehavior(AbstractTextComponent textComponent) {
        this.textComponent = textComponent;
        textComponent.setOutputMarkupId(true);
        textComponent.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("form-control");
                oldClasses.add("datetimepicker-input");
                return oldClasses;
            }
        });

        textComponent.add(new AttributeModifier("data-target", "#" + textComponent.getMarkupId()));
        format = (((AbstractTextComponent.ITextFormatProvider) textComponent).getTextFormat());
        locale = Session.get().getLocale().getLanguage();
    }

    @Override
    public void bind(Component component) {
        super.bind(component);
        if (textComponent.equals(component)) {
            textComponent.add(new AttributeModifier("data-toggle", "datetimepicker"));
        }
        component.setOutputMarkupId(true);
        tempusdominusId = component.getMarkupId();
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        String value = textComponent.getValue();
        response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("tempusdominus-bootstrap-4/current/js/tempusdominus-bootstrap-4.min.js") {
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
                return ImmutableList.of(CssHeaderItem.forReference(new WebjarsCssResourceReference("bootstrap/current/css/bootstrap.min.css")));
            }
        }));
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').datetimepicker(" + toJSON() + ")", component.getMarkupId())));
    }


    public JSONObject toJSON() {
        return new JSONObject().put("locale", locale)
                .put("format", Optional.ofNullable(format)/*.filter(Boolean::booleanValue).map(b -> (Object) "L")*/.map(fmt->new JSONFunction(String.format("moment().toMomentFormatString(\"%s\")", fmt))).orElse(null))
                .put("buttons", buttons.toJSON())
                .put("date", new JSONFunction(String.format("moment(\"%s\",moment().toMomentFormatString(\"%s\"))", textComponent.getValue(), format)))
                .put("useStrict", useStrict)
                .put("useCurrent", useCurrent)
                ;
    }

    public String bindMinDateScript(ITempusdominus bindMinDate) {
        return String.format("$(\"#%s\").on(\"change.datetimepicker\",function(e){" +
                "$(\"#%s\").datetimepicker(\"minDate\", e.date);" +
                "})", bindMinDate.getTempusdominusId(), tempusdominusId);
    }

    public String bindMaxDateScript(ITempusdominus bindMaxDate) {
        return String.format("$(\"#%s\").on(\"change.datetimepicker\",function(e){" +
                "$(\"#%s\").datetimepicker(\"maxDate\", e.date);" +
                "})", bindMaxDate.getTempusdominusId(), tempusdominusId);
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    static class Buttons implements Serializable, IClusterable {

        Boolean showToday = true;

        Boolean showClear = true;

        Boolean showClose = true;


        public JSONObject toJSON() {
            return new JSONObject().put("showToday", showToday)
                    .put("showClear", showClear)
                    .put("showClose", showClose);
        }
    }
}
