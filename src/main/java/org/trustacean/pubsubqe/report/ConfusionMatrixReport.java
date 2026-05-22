package org.trustacean.pubsubqe.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.djutils.event.Event;
import org.trustacean.pubsubqe.domain.Subscriber;
import org.trustacean.pubsubqe.stats.MatchResult;

public class ConfusionMatrixReport extends Report {

    private final Map<Subscriber, Integer> truePositiveCounts = new HashMap<>();
    private final Map<Subscriber, Integer> falsePositiveCounts = new HashMap<>();
    private final Map<Subscriber, Integer> falseNegativeCounts = new HashMap<>();
    private final Map<Subscriber, Integer> trueNegativeCounts = new HashMap<>();

    public ConfusionMatrixReport(String outFileName) {
        super(outFileName);
    }

    @Override
    public void notify(Event event) {
        if (event.getType().equals(MatchResult.MATCH_RESULT_EVENT)) {
            MatchResult result = (MatchResult) event.getContent();
            updateConfusionMatrix(result);
        }
        super.notify(event);
    }

    private void updateConfusionMatrix(MatchResult result) {
        Subscriber subscriber = result.getSubscriber();
        boolean matches = result.isMatches();
        boolean isRelevant = result.isRelevant();

        if (matches && isRelevant) {
            mapIncrement(truePositiveCounts, subscriber);
        } else if (matches) {
            mapIncrement(falsePositiveCounts, subscriber);
        } else if (isRelevant) {
            mapIncrement(falseNegativeCounts, subscriber);
        } else {
            mapIncrement(trueNegativeCounts, subscriber);
        }
    }

    @Override
    public void done() {
        Set<Subscriber> allSubscribers = new HashSet<>();

        allSubscribers.addAll(truePositiveCounts.keySet());
        allSubscribers.addAll(falsePositiveCounts.keySet());
        allSubscribers.addAll(falseNegativeCounts.keySet());
        allSubscribers.addAll(trueNegativeCounts.keySet());

        for (Subscriber subscriber : allSubscribers) {
            int tp = truePositiveCounts.getOrDefault(subscriber, 0);
            int fp = falsePositiveCounts.getOrDefault(subscriber, 0);
            int fn = falseNegativeCounts.getOrDefault(subscriber, 0);
            int tn = trueNegativeCounts.getOrDefault(subscriber, 0);

            write("Subscriber: " + subscriber.getTopic());
            write("TP: " + tp);
            write("FP: " + fp);
            write("FN: " + fn);
            write("TN: " + tn);
        }

        super.done();
    }

}
