package com.bochkov.bootstrap.tempusdominus.localdate;

import com.bochkov.bootstrap.tempusdominus.TempusdominusBehavior;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.time.LocalDate;
import java.time.format.FormatStyle;

public class LocalDateTextFieldCalendar extends LocalDateTextField {

    public LocalDateTextFieldCalendar(String id, IModel<LocalDate> model, String dateTimePattern) {
        super(id, model, dateTimePattern);
    }

    public LocalDateTextFieldCalendar(String id, String dateTimePattern) {
        super(id, dateTimePattern);
    }

    public LocalDateTextFieldCalendar(String id, IModel<LocalDate> model, FormatStyle dateStyle) {
        super(id, model, dateStyle);
    }

    public LocalDateTextFieldCalendar(String id, FormatStyle dateStyle) {
        super(id, dateStyle);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TempusdominusBehavior(this));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(this.getClass(), "LocalDateTextFieldCalendar.js")));
        response.render(OnDomReadyHeaderItem.forScript(String.format("createLocalDateInputGroup('#%s');", getMarkupId())));
    }
}
