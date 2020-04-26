package com.bochkov.bootstrap.tempusdominus;

import org.apache.wicket.extensions.markup.html.form.datetime.LocalTimeTextField;
import org.apache.wicket.model.IModel;

import java.time.LocalTime;
import java.time.format.FormatStyle;

public class LocalTimeField extends LocalTimeTextField {
    public LocalTimeField(String id, IModel<LocalTime> model, String timePattern) {
        super(id, model, timePattern);
    }

    public LocalTimeField(String id, String datePattern) {
        super(id, datePattern);
    }

    public LocalTimeField(String id, IModel<LocalTime> model, FormatStyle timeStyle) {
        super(id, model, timeStyle);
    }

    public LocalTimeField(String id, FormatStyle timeStyle) {
        super(id, timeStyle);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new TempusdominusBehavior(this));
    }
}
