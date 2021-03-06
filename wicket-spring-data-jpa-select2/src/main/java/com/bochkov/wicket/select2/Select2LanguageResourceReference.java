package com.bochkov.wicket.select2;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * {@link JavaScriptResourceReference} for a select2 i18n file,
 * please see i18n resources folder for supported languages.
 *
 * @author Tom Götz (tom@decoded.de)
 */
class Select2LanguageResourceReference extends WebjarsJavaScriptResourceReference {

    private static final long serialVersionUID = 1L;

    private static final String resourceName = "select2/current/js/i18n/%s.js";

    private static final String defaultLanguage = "en";

    /**
     * Instantiates a new Select 2 language resource reference.
     *
     * @param language i18n file to load (e.g. "en", "de", "fr" ...)
     */
    Select2LanguageResourceReference(String language) {
        super(getResourceName(language));
    }

    /**
     * Returns the resource name of the i18n file, uses a fallback to defaultLanguage
     * if requested resource file does not exist
     *
     * @param language i18n file to load
     * @return resource name
     */
    private static String getResourceName(String language) {
        try {
            String name = String.format(resourceName, language);
            if (Select2LanguageResourceReference.class.getResource(name) != null) {
                return name;
            }
        } catch (Exception ignore) {
            // noop
        }
        return String.format(resourceName, defaultLanguage);
    }
}
