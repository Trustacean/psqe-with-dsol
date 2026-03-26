package org.trustacean.pubsubqe.core;

import java.util.ArrayList;
import java.util.List;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.LocalEventProducer;
import org.trustacean.pubsubqe.stats.MatchResult;

public class Broker extends LocalEventProducer implements EventListener {

    private final List<Subscriber> subscribers = new ArrayList<>();

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void notify(Event event) {
        Message msg = (Message) event.getContent();

        for (Subscriber subscriber : subscribers) {
            boolean matches = subscriber.matches(msg);
            boolean isRelevant = msg.getContext().equals(subscriber.getKeywordStr());
            
            if (matches) {
                subscriber.receive(msg);
                fireEvent(Message.MESSAGE_DELIVERED_EVENT, subscriber);
            }

            fireEvent(MatchResult.MATCH_RESULT_EVENT, new MatchResult(subscriber, matches, isRelevant));
        }
    }
}
