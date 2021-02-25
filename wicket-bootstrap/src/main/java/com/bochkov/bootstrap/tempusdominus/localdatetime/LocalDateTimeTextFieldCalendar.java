package com.bochkov.bootstrap.tempusdominus.localdatetime;

import com.bochkov.bootstrap.tempusdominus.TempusdominusBehavior;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTimeTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.time.LocalDateTime;
import java.time.format.FormatStyle;

public class LocalDateTimeTextFieldCalendar extends LocalDateTimeTextField {

    public LocalDateTimeTextFieldCalendar(String id, IModel<LocalDateTime> model, String dateTimePattern) {
        super(id, model, dateTimePattern);
    }

    public LocalDateTimeTextFieldCalendar(String id, String dateTimePattern) {
        super(id, dateTimePattern);
    }

    public LocalDateTimeTextFieldCalendar(String id, IModel<LocalDateTime> model, FormatStyle dateStyle, FormatStyle timeStyle) {
        super(id, model, dateStyle, timeStyle);
    }

    public LocalDateTimeTextFieldCalendar(String id, FormatStyle dateStyle, FormatStyle timeStyle) {
        super(id, dateStyle, timeStyle);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TempusdominusBehavior(this));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(this.getClass(), "LocalDateTimeTextFieldCalendar.js")));
        response.render(OnDomReadyHeaderItem.forScript(String.format("createInputGroup('#%s');", getMarkupId())));
    }
}
