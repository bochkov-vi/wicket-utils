package com.bochkov.bootstrap.tempusdominus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public abstract class Calendar<T extends Serializable> extends GenericPanel<T> implements AbstractTextComponent.ITextFormatProvider, ITempusdominus {

    TextField<T> textField = createInput("input-text");

    TempusdominusBehavior behavior = new TempusdominusBehavior(textField);

    Button button = new Button("button");

    public Calendar(String id) {
        super(id);
    }

    public Calendar(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("input-group");
                oldClasses.add("date");
                return oldClasses;
            }
        });
        add(new AttributeModifier("data-target-input", "nearest"));
        add(textField);
        add(button);
        button.add(new AttributeModifier("data-target", String.format("#%s", getMarkupId())));
        textField.add(new AttributeModifier("data-target", String.format("#%s", getMarkupId())));
        textField.setModel(getModel());
        add(behavior.setDateOnly(true));
    }

    public abstract <F extends TextField<T> & AbstractTextComponent.ITextFormatProvider> F createInput(String id);


    @Override
    public String getTextFormat() {
        return ((AbstractTextComponent.ITextFormatProvider) textField).getTextFormat();
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
