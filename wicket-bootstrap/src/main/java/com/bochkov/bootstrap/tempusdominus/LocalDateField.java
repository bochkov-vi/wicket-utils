package com.bochkov.bootstrap.tempusdominus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;

import java.time.LocalDate;
import java.time.format.FormatStyle;

@Getter
@Setter
@Accessors(chain = true)
public class LocalDateField extends LocalDateTextField implements ITempusdominus {

    TempusdominusBehavior behavior = new TempusdominusBehavior(this);

    public LocalDateField(String id, String pattern) {
        super(id, pattern);
    }

    public LocalDateField(String id, IModel<LocalDate> model, String pattern) {
        super(id, model, pattern);
    }

    public LocalDateField(String id, IModel<LocalDate> model, String formatPattern, String parsePattern) {
        super(id, model, formatPattern, parsePattern);
    }

    public LocalDateField(String id, FormatStyle dateStyle) {
        super(id, dateStyle);
    }

    public LocalDateField(String id, IModel<LocalDate> model, FormatStyle dateStyle) {
        super(id, model, dateStyle);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(behavior);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

    }

    @Override
    public String getTempusdominusId() {
        return behavior.getTempusdominusId();
    }

    public String bindMinDateScript(ITempusdominus bindMinDate) {
        return behavior.bindMinDateScript(bindMinDate);
    }

    public String bindMaxDateScript(ITempusdominus bindMaxDate) {
        return behavior.bindMaxDateScript(bindMaxDate);
    }
}
