package com.bochkov.bootstrap.tempusdominus;

import com.github.openjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.json.JSONFunction;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.util.io.IClusterable;

import java.io.Serializable;
import java.time.temporal.Temporal;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class TempusdominusBehavior extends TempusdominusResources implements ITempusdominus {

    String locale;

    Buttons buttons = new Buttons();

    Boolean useStrict = true;

    Boolean dateOnly;

    Boolean useCurrent;

    Temporal minDate;

    Temporal maxDate;

    Integer stepping;

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


    public JSONObject toJSON() {
        String format = ((AbstractTextComponent.ITextFormatProvider) textComponent).getTextFormat();
        return new JSONObject().put("locale", locale)
                .put("format", Optional.ofNullable(format)/*.filter(Boolean::booleanValue).map(b -> (Object) "L")*/.map(fmt -> new JSONFunction(String.format("moment().toMomentFormatString(\"%s\")", fmt))).orElse(null))
                .put("buttons", buttons.toJSON())
                .put("date", new JSONFunction(String.format("moment(\"%s\",moment().toMomentFormatString(\"%s\"))", textComponent.getValue(), format)))
                .put("useStrict", useStrict)
                .put("useCurrent", useCurrent)
                .put("stepping", stepping)
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

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').datetimepicker(" + toJSON() + ")", component.getMarkupId())));
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
