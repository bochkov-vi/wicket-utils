package com.bochkov.wicket.component.select2.data;

import java.util.Collection;

/**
 * The type Pageable string choice provider.
 */
public abstract class PageableStringChoiceProvider extends PageableChoiceProvider<String> {


    @Override
    public String getDisplayValue(String object) {
        return object;
    }

    @Override
    public String getIdValue(String object) {
        return object;
    }

    @Override
    public Collection<String> toChoices(Collection<String> ids) {
        return ids;
    }
}
