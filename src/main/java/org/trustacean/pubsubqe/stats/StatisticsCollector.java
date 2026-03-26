package org.trustacean.pubsubqe.stats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.trustacean.pubsubqe.core.Broker;
import org.trustacean.pubsubqe.core.Message;
import org.trustacean.pubsubqe.core.Subscriber;

import nl.tudelft.simulation.dsol.simulators.DevsSimulator;

public class StatisticsCollector implements EventListener {

    private int eventCount = 0;
    private final Set<String> contexts;
    private final Map<String, Integer> contextCounts;

    public StatisticsCollector() {
        contexts = new HashSet<>();
        contextCounts = new HashMap<>();
    }

    @Override
    public void notify(Event event) {
        eventCount++;

        if (event.getType() == Message.MESSAGE_EVENT) {
            account(event);
        }

        if (event.getType() == DevsSimulator.STOP_EVENT) {
            printStatistics();
        }
    }

    private void account(Event event) {
        Message msg = (Message) event.getContent();

        contexts.add(msg.getContext());
        contextCounts.put(msg.getContext(), contextCounts.getOrDefault(msg.getContext(), 0) + 1);
    }

    private void printStatistics() {
        System.out.println("Ts Stopped");
        System.out.println("Events count : " + eventCount);

        for (String context : contexts) {
            System.out.print("Context: " + context + " - ");
            System.out.println("Messages: " + contextCounts.get(context));
        }
        System.out.println();
        Map<Subscriber, Integer> subscriberMessageCounts = Broker.getSubscriberMessageCounts();
        for (Subscriber subscriber : subscriberMessageCounts.keySet()) {
            System.out.print("Subscriber: " + Arrays.toString(subscriber.getKeywords()) + " - ");
            System.out.println("Messages: " + subscriberMessageCounts.get(subscriber));
        }

        Map<Subscriber, Integer> truePositiveCounts = Broker.getTruePositiveCounts();
        Map<Subscriber, Integer> falsePositiveCounts = Broker.getFalsePositiveCounts();
        Map<Subscriber, Integer> falseNegativeCounts = Broker.getFalseNegativeCounts();
        Map<Subscriber, Integer> trueNegativeCounts = Broker.getTrueNegativeCounts();
        Integer overallTruePositiveCount = Broker.getOverallTruePositiveCount();
        Integer overallFalsePositiveCount = Broker.getOverallFalsePositiveCount();
        Integer overallFalseNegativeCount = Broker.getOverallFalseNegativeCount();
        Integer overallTrueNegativeCount = Broker.getOverallTrueNegativeCount();

        for (Subscriber subscriber : subscriberMessageCounts.keySet()) {
            int tp = truePositiveCounts.getOrDefault(subscriber, 0);
            int fp = falsePositiveCounts.getOrDefault(subscriber, 0);
            int fn = falseNegativeCounts.getOrDefault(subscriber, 0);
            int tn = trueNegativeCounts.getOrDefault(subscriber, 0);

            System.out.println("Subscriber: " + Arrays.toString(subscriber.getKeywords()));
            System.out.println("True Positives: " + tp);
            System.out.println("False Positives: " + fp);
            System.out.println("False Negatives: " + fn);
            System.out.println("True Negatives: " + tn);
            System.out.println();
        }

        System.out.println("Overall True Positives: " + overallTruePositiveCount);
        System.out.println("Overall False Positives: " + overallFalsePositiveCount);
        System.out.println("Overall False Negatives: " + overallFalseNegativeCount);
        System.out.println("Overall True Negatives: " + overallTrueNegativeCount);
    }
}
