package org.trustacean.pubsubqe.core;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

public class Message
{
    private final String context;
    private final String text;
    public static final MetaData META_DATA = 
        new MetaData("MESSAGE_EVENT", "dataset message event", 
            new ObjectDescriptor("Text", "Text data", Message.class));
    public static final EventType MESSAGE_EVENT = 
        new EventType("MESSAGE_EVENT", META_DATA);

    public Message(String Context, String text)
    {
        this.context = Context;
        this.text = text;
    }
    
    public String getContext()
    {
        return context;
    }

    public String getText()
    {
        return text;
    }
}
