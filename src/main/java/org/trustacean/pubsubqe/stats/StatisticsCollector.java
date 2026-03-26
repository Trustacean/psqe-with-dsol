package org.trustacean.pubsubqe.stats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.trustacean.pubsubqe.core.Message;
import org.trustacean.pubsubqe.core.Subscriber;

import nl.tudelft.simulation.dsol.simulators.DevsSimulator;

public class StatisticsCollector implements EventListener {

    private int eventCount = 0;
    private final Set<String> contexts;
    private final Map<String, Integer> contextCounts;
    private final Map<Subscriber, Integer> subscriberMessageCounts = new HashMap<>();

    private final Map<Subscriber, Integer> truePositiveCounts = new HashMap<>();
    private final Map<Subscriber, Integer> falsePositiveCounts = new HashMap<>();
    private final Map<Subscriber, Integer> falseNegativeCounts = new HashMap<>();
    private final Map<Subscriber, Integer> trueNegativeCounts = new HashMap<>();

    private int overallTruePositiveCount = 0;
    private int overallFalsePositiveCount = 0;
    private int overallFalseNegativeCount = 0;
    private int overallTrueNegativeCount = 0;

    public StatisticsCollector() {
        contexts = new HashSet<>();
        contextCounts = new HashMap<>();
    }

    @Override
    public void notify(Event event) {
        eventCount++;

        if (event.getType().equals(Message.MESSAGE_PUBLISHED_EVENT)) {
            account(event);
        }

        if (event.getType().equals(Message.MESSAGE_DELIVERED_EVENT)) {
            Subscriber subscriber = (Subscriber) event.getContent();
            increment(subscriberMessageCounts, subscriber);
        }

        if (event.getType().equals(MatchResult.MATCH_RESULT_EVENT)) {
            MatchResult result = (MatchResult) event.getContent();
            updateConfusionMatrix(result);
        }

        if (event.getType().equals(DevsSimulator.STOP_EVENT)) {
            printStatistics();
        }
    }

    private void account(Event event) {
        Message msg = (Message) event.getContent();

        contexts.add(msg.getContext());
        contextCounts.put(msg.getContext(), contextCounts.getOrDefault(msg.getContext(), 0) + 1);
    }

    private void updateConfusionMatrix(MatchResult result) {
        Subscriber subscriber = result.getSubscriber();
        boolean matches = result.isMatches();
        boolean isRelevant = result.isRelevant();

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

    private void printStatistics() {
        System.out.println("Ts Stopped");
        System.out.println("Calculating statistics...");
        System.out.println("Events count : " + eventCount);

        for (String context : contexts) {
            System.out.print("Context: " + context + " - ");
            System.out.println("Messages: " + contextCounts.get(context));
        }
        System.out.println();
        
        for (Subscriber subscriber : subscriberMessageCounts.keySet()) {
            System.out.print("Subscriber: " + Arrays.toString(subscriber.getKeywords()) + " - ");
            System.out.println("Messages: " + subscriberMessageCounts.get(subscriber));
        }
        System.out.println();

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
    }
}
