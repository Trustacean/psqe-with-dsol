package org.trustacean.pubsubqe.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.LocalEventProducer;

public class Broker extends LocalEventProducer implements EventListener {

    private final List<Subscriber> subscribers = new ArrayList<>();
    private static final Map<Subscriber, Integer> subscriberMessageCounts = new HashMap<>();
    private static final Map<Subscriber, Integer> truePositiveCounts = new HashMap<>();
    private static final Map<Subscriber, Integer> falsePositiveCounts = new HashMap<>();
    private static final Map<Subscriber, Integer> falseNegativeCounts = new HashMap<>();
    private static final Map<Subscriber, Integer> trueNegativeCounts = new HashMap<>();
    private static int overallTruePositiveCount = 0;
    private static int overallFalsePositiveCount = 0;
    private static int overallFalseNegativeCount = 0;
    private static int overallTrueNegativeCount = 0;

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void notify(Event event) {
        Message msg = (Message) event.getContent();
        // System.out.println("Broker recieved: " + msg.getText());
        for (Subscriber subscriber : subscribers) {
            boolean matches = subscriber.matches(msg);
            boolean isRelevant = msg.getContext().equals(subscriber.getKeywordStr());
            // System.out.println("Subscriber " + subscriber + " deemed message as " + subscriber.matches(msg));
            if (matches) {
                subscriber.receive(msg);
                increment(subscriberMessageCounts, subscriber);
            }
        updateConfusionMatrix(subscriber, matches, isRelevant);
        }
    }

    private void updateConfusionMatrix(Subscriber subscriber, boolean matches, boolean isRelevant) {
        if (matches && isRelevant) {
            increment(truePositiveCounts, subscriber);
            overallTruePositiveCount++;
        } else if (matches) {
            increment(falsePositiveCounts, subscriber);
            overallFalsePositiveCount++;
        } else if (isRelevant) {
            increment(falseNegativeCounts, subscriber);
            overallFalseNegativeCount++;
        } else {
            increment(trueNegativeCounts, subscriber);
            overallTrueNegativeCount++;
        }
    }

    private void increment(Map<Subscriber, Integer> map, Subscriber subscriber) {
        map.put(subscriber, map.getOrDefault(subscriber, 0) + 1);
    }

    public static Map<Subscriber, Integer> getSubscriberMessageCounts() {
        return subscriberMessageCounts;
    }

    public static Map<Subscriber, Integer> getTruePositiveCounts() {
        return truePositiveCounts;
    }

    public static Map<Subscriber, Integer> getFalsePositiveCounts() {
        return falsePositiveCounts;
    }

    public static Map<Subscriber, Integer> getFalseNegativeCounts() {
        return falseNegativeCounts;
    }

    public static Map<Subscriber, Integer> getTrueNegativeCounts() {
        return trueNegativeCounts;
    }

    public static Integer getOverallTruePositiveCount() {
        return overallTruePositiveCount;
    }

    public static Integer getOverallFalsePositiveCount() {
        return overallFalsePositiveCount;
    }

    public static Integer getOverallFalseNegativeCount() {
        return overallFalseNegativeCount;
    }

    public static Integer getOverallTrueNegativeCount() {
        return overallTrueNegativeCount;
    }
}
