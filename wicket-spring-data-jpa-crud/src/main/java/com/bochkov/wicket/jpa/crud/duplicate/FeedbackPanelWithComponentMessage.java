package com.bochkov.wicket.jpa.crud.duplicate;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class FeedbackPanelWithComponentMessage extends FeedbackPanel {

    public FeedbackPanelWithComponentMessage(String id) {
        super(id);
    }

    public FeedbackPanelWithComponentMessage(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    @Override
    protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
        Component component = null;
        if (message instanceof FeedbackMessageComponent) {
            component = ((FeedbackMessageComponent) message).createDisplayComponent(id);
        } else {
            component = super.newMessageDisplayComponent(id, message);
        }
        return component;
    }
}
