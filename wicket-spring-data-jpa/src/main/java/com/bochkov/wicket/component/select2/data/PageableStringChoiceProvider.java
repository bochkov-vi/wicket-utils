package com.bochkov.wicket.component.select2.data;

import org.danekja.java.util.function.serializable.SerializableBiFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The type Pageable string choice provider.
 */
public abstract class PageableStringChoiceProvider extends PageableChoiceProvider<String> {


    public static PageableStringChoiceProvider of(SerializableBiFunction<String, Pageable, Page<String>> finder) {
        PageableStringChoiceProvider provider = new PageableStringChoiceProvider() {
            @Override
            public Page<String> findByMask(String term, Pageable pageRequest) {
                return finder.apply(term, pageRequest);
            }
        };
        return provider;
    }

    @Override
    public String getDisplayValue(String object) {
        return object;
    }

    @Override
    public String getIdValue(String object) {
        return object;
    }

    @Override
    public String toChoise(String id) {
        return id;
    }
}
