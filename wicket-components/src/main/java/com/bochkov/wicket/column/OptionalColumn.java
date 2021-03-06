package com.bochkov.wicket.column;

import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Optional;

public class OptionalColumn<T, S> extends LambdaColumn<T, S> {

    SerializableFunction<Optional<T>, Optional> function;

    public static <T> OptionalColumn<T, String> of(IModel<String> displayModel, SerializableFunction<Optional<T>, Optional> function) {
        return new OptionalColumn<>(displayModel, function);
    }

    public static <T, S> OptionalColumn<T, S> of(IModel<String> displayModel, S sortProperty, SerializableFunction<Optional<T>, Optional> function) {
        return new OptionalColumn<T, S>(displayModel, sortProperty, function);
    }


    public OptionalColumn(IModel<String> displayModel, SerializableFunction<Optional<T>, Optional> function) {
        super(displayModel, (SerializableFunction<T, Object>) t -> function.apply(Optional.ofNullable(t)).orElse(null));
        this.function = function;
    }

    public OptionalColumn(IModel<String> displayModel, S sortProperty, SerializableFunction<Optional<T>, Optional> function) {
        super(displayModel, sortProperty, (SerializableFunction<T, Object>) t -> function.apply(Optional.ofNullable(t)).orElse(null));
        this.function = function;
    }
}
