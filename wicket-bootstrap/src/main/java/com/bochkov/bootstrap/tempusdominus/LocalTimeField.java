package com.bochkov.bootstrap.tempusdominus;

import org.apache.wicket.extensions.markup.html.form.datetime.LocalTimeTextField;
import org.apache.wicket.model.IModel;

import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.time.temporal.Temporal;

public class LocalTimeField extends LocalTimeTextField {
    TempusdominusBehavior behavior;

    public LocalTimeField(String id, IModel<LocalTime> model, String timePattern) {
        super(id, model, timePattern);
        add(behavior = new TempusdominusBehavior(this));

    }

    public LocalTimeField(String id, String datePattern) {
        super(id, datePattern);
        add(behavior = new TempusdominusBehavior(this));

    }

    public LocalTimeField(String id, IModel<LocalTime> model, FormatStyle timeStyle) {
        super(id, model, timeStyle);
        add(behavior = new TempusdominusBehavior(this));

    }

    public LocalTimeField(String id, FormatStyle timeStyle) {
        super(id, timeStyle);
        add(behavior = new TempusdominusBehavior(this));

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }


    public TempusdominusBehavior.Buttons getButtons() {
        return behavior.getButtons();
    }

    public LocalTimeField setButtons(TempusdominusBehavior.Buttons buttons) {
        behavior.setButtons(buttons);
        return this;
    }

    public Boolean getUseStrict() {
        return behavior.getUseStrict();
    }

    public LocalTimeField setUseStrict(Boolean useStrict) {
        behavior.setUseStrict(useStrict);
        return this;
    }

    public Boolean getDateOnly() {
        return behavior.getDateOnly();
    }

    public LocalTimeField setDateOnly(Boolean dateOnly) {
        behavior.setDateOnly(dateOnly);
        return this;
    }

    public Boolean getUseCurrent() {
        return behavior.getUseCurrent();
    }

    public LocalTimeField setUseCurrent(Boolean useCurrent) {
        behavior.setUseCurrent(useCurrent);
        return this;
    }

    public Temporal getMinDate() {
        return behavior.getMinDate();
    }

    public LocalTimeField setMinDate(Temporal minDate) {
        behavior.setMinDate(minDate);
        return this;
    }

    public Temporal getMaxDate() {
        return behavior.getMaxDate();
    }

    public LocalTimeField setMaxDate(Temporal maxDate) {
        behavior.setMaxDate(maxDate);
        return this;
    }

    public Integer getStepping() {
        return behavior.getStepping();
    }

    public LocalTimeField setStepping(Integer stepping) {
        behavior.setStepping(stepping);
        return this;
    }

    public String getTempusdominusId() {
        return behavior.getTempusdominusId();
    }
}
