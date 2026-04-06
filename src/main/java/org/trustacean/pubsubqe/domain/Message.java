package org.trustacean.pubsubqe.domain;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

public class Message {

    private final String context;
    private final String text;
    public static final EventType MESSAGE_PUBLISHED_EVENT
            = new EventType("MESSAGE_PUBLISHED_EVENT",
                    new MetaData("MESSAGE_PUBLISHED_EVENT", "dataset message event",
                            new ObjectDescriptor("Text", "Text data", Message.class)));
    public static final EventType MESSAGE_DELIVERED_EVENT = new EventType("MESSAGE_DELIVERED_EVENT",
            new MetaData("MESSAGE_DELIVERED_EVENT", "message delivered event",
                    new ObjectDescriptor("Text", "Text data", Subscriber.class)));

    public Message(String Context, String text) {
        this.context = Context;
        this.text = text;
    }

    public String getContext() {
        return context;
    }

    public String getText() {
        return text;
    }
}
