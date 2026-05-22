package org.trustacean.pubsubqe.domain;

import org.djutils.event.Event;
import org.djutils.event.LocalEventProducer;
import org.trustacean.pubsubqe.domain.gtrm.GlobalTermRelationshipModel;

public class Publisher extends LocalEventProducer {

    public void publish(Message msg) {
        String normalizedText = String.join(" ", GlobalTermRelationshipModel.normalize(msg.getText()));
        msg.setText(normalizedText);
        // System.out.println("Publisher sent: " + msg.getText());
        fireEvent(new Event(Message.MESSAGE_PUBLISHED_EVENT, msg));
    }
}
