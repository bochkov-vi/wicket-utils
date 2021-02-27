package com.bochkov.wicket.jpa.crud.duplicate;

import org.apache.wicket.Component;
import org.apache.wicket.IRequestListener;
import org.apache.wicket.feedback.FeedbackMessage;

import java.io.Serializable;

public class DuplicateFeedbackMessage extends FeedbackMessage implements IRequestListener {
    /**
     * Construct using fields.
     *
     * @param reporter The message reporter
     * @param message  The actual message. Must not be <code>null</code>.
     * @param level
     */
    public DuplicateFeedbackMessage(Component reporter, Serializable message, int level) {
        super(reporter, message, level);
    }

    @Override
    public boolean rendersPage() {
        return false;
    }

    @Override
    public void onRequest() {
        this.markRendered();
    }
}
