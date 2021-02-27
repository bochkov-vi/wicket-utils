package com.bochkov.wicket.jpa.crud;

import com.bochkov.bootstrap.BootstrapBehavior;
import com.bochkov.fontawesome.FontAwesomeBehavior;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;

import java.time.LocalDate;
import java.util.Optional;

public class BasePage<T> extends GenericWebPage<T> {


    public BasePage() {
    }

    public BasePage(IModel<T> model) {
        super(model);
    }

    public BasePage(PageParameters parameters) {
        super(parameters);
    }

    public static Optional<LocalDate> date(StringValue value) {
        Optional<LocalDate> date = Optional.ofNullable(value)
                .map(StringValue::toOptionalString)
                .filter(str -> !Strings.isEmpty(str))
                .map(str -> {
                    try {
                        return Application.get().getConverterLocator().getConverter(LocalDate.class).convertToObject(str, Session.get().getLocale());
                    } catch (ConversionException e) {
                        return null;
                    }
                });
        return date;
    }

    public static String string(LocalDate value) {
        return Application.get().getConverterLocator().getConverter(LocalDate.class).convertToString(value, Session.get().getLocale());
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new BootstrapBehavior());
        add(new FontAwesomeBehavior());

    }

}
