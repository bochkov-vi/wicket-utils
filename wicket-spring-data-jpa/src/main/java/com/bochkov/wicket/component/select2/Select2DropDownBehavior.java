package com.bochkov.wicket.component.select2;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.wicketstuff.select2.JQuery;
import org.wicketstuff.select2.Select2ResourcesBehavior;
import org.wicketstuff.select2.Settings;

/**
 * The type Select 2 drop down behavior.
 */
public class Select2DropDownBehavior extends Behavior {

    private Settings settings = new Settings();

    /**
     * For single choice select 2 drop down behavior.
     *
     * @return the select 2 drop down behavior
     */
    public static Select2DropDownBehavior forSingleChoice() {
        return new Select2DropDownBehavior();
    }

    /**
     * For multi choice select 2 drop down behavior.
     *
     * @return the select 2 drop down behavior
     */
    public static Select2DropDownBehavior forMultiChoice() {
        Select2DropDownBehavior select2Behavior = new Select2DropDownBehavior();
        select2Behavior.getSettings().setMultiple(true);
        return select2Behavior;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        new Select2ResourcesBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);

                // render theme related resources if any
                if (settings.getTheme() != null) {
                    settings.getTheme().renderHead(component, response);
                }

                // include i18n resource file
                response.render(JavaScriptHeaderItem.forReference(
                        new Select2LanguageResourceReference(settings.getLanguage())));
            }
        }.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript(JQuery.execute("$('#%s').select2(%s);",
                component.getMarkupId(), getSettings().toJson())));
    }

    @Override
    public void bind(Component component) {
        component.setOutputMarkupId(true);
    }

    /**
     * Gets settings.
     *
     * @return the settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Sets settings.
     *
     * @param settings the settings
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
