package org.trustacean.pubsubqe.core;

import org.djutils.event.Event;
import org.djutils.event.LocalEventProducer;

public class Publisher extends LocalEventProducer {

    public void publish(Message msg) {
        // System.out.println("Publisher sent: " + msg.getText());
        fireEvent(new Event(Message.MESSAGE_EVENT, msg));
    }
}
