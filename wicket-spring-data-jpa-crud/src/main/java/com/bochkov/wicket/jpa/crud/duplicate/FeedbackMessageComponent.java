package com.bochkov.wicket.jpa.crud.duplicate;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;

import java.io.Serializable;

public abstract class FeedbackMessageComponent extends FeedbackMessage {


    /**
     * Construct using fields.
     *
     * @param reporter The message reporter
     * @param message  The actual message. Must not be <code>null</code>.
     * @param level
     */
    public FeedbackMessageComponent(Component reporter, Serializable message, int level) {
        super(reporter, message, level);
    }

    public abstract Component createDisplayComponent(String id);
}
