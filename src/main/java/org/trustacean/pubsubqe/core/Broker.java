package org.trustacean.pubsubqe.core;

import java.util.ArrayList;
import java.util.List;

import org.djutils.event.Event;
import org.djutils.event.EventListener;

public class Broker implements EventListener{
    private final List<Subscriber> subscribers = new ArrayList<>();

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void notify(Event event) {
        Message msg = (Message) event.getContent();
        System.out.println("Broker recieved: " + msg.getText());
        for (Subscriber subscriber : subscribers) {
            System.out.println("Subscriber " + subscriber + " deemed message as " + subscriber.matches(msg));
            if (subscriber.matches(msg)) {
                subscriber.recieve(msg);
            }
        }
    }
}
