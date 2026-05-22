package org.trustacean.pubsubqe.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.LocalEventProducer;
import org.trustacean.pubsubqe.Parameters;
import org.trustacean.pubsubqe.domain.gtrm.GlobalTermRelationshipModel;
import org.trustacean.pubsubqe.domain.gtrm.RankedTerm;
import org.trustacean.pubsubqe.stats.MatchResult;

public class Broker extends LocalEventProducer implements EventListener {
    private final GlobalTermRelationshipModel gtrm;
    private final List<Subscriber> subscribers = new ArrayList<>();

    public Broker(GlobalTermRelationshipModel gtrm) {
        this.gtrm = gtrm;
        addListener(this.gtrm, Message.MESSAGE_PUBLISHED_EVENT);
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
        for (String term : subscriber.getKeywords()) {
            this.gtrm.addQueryTerm(term);
        }
    }

    @Override
    public void notify(Event event) {
        if (event.getType().equals(Message.MESSAGE_PUBLISHED_EVENT)) {
            processMessage((Message) event.getContent());
        }
    }

    private void processMessage(Message msg) {
        fireEvent(Message.MESSAGE_PUBLISHED_EVENT, msg);

        for (Subscriber subscriber : this.subscribers) {

            boolean matches = matchesWithRelevancyScore(subscriber, msg);
            boolean isRelevant = msg.getContext().equals(subscriber.getTopic());

            if (matches) {
                subscriber.receive(msg);
                fireEvent(Message.MESSAGE_DELIVERED_EVENT, subscriber);
            }

            fireEvent(MatchResult.MATCH_RESULT_EVENT,
                    new MatchResult(subscriber, matches, isRelevant));
        }
    }

    private boolean matches(Subscriber subscriber, Message msg) {
        Set<String> words = Arrays.stream(msg.getText().split("\\s+"))
                .collect(Collectors.toSet());

        for (String keyword : subscriber.getKeywords()) {
            if (words.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesOrExpansion(Subscriber subscriber, Message msg) {
        Set<String> words = Arrays.stream(msg.getText().split("\\s+"))
                .collect(Collectors.toSet());

        for (String keyword : subscriber.getKeywords()) {

            // 1. Direct match
            if (words.contains(keyword)) {
                return true;
            }

            // 2. Expanded match
            var expanded = gtrm.expand(keyword, Parameters.EXPANSION_THRESHOLD);

            for (RankedTerm rankedTerm : expanded) {
                if (words.contains(rankedTerm.getTerm())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesAndExpansion(Subscriber subscriber, Message msg) {
        Set<String> words = Arrays.stream(msg.getText().split("\\s+"))
                .collect(Collectors.toSet());

        for (String keyword : subscriber.getKeywords()) {

            boolean satisfied = false;

            if (words.contains(keyword)) {
                satisfied = true;
            } else {
                var expanded = gtrm.expand(keyword, Parameters.EXPANSION_THRESHOLD);

                for (RankedTerm rankedTerm : expanded) {
                    if (words.contains(rankedTerm.getTerm())) {
                        satisfied = true;
                        break;
                    }
                }
            }

            if (!satisfied)
                return false;
        }

        return true;
    }

    private boolean matchesWithRelevancyScore(Subscriber subscriber, Message msg) {
        Set<String> words = Arrays.stream(msg.getText().split("\\s+"))
                .collect(Collectors.toSet());

        int subscriberKeywordCount = subscriber.getKeywords().size();
        double score = 0; // Score must be between 0 and 1

        for (String keyword : subscriber.getKeywords()) {
            if (words.contains(keyword)) {
                score += 1.0 / subscriberKeywordCount; // Direct match contributes full score
            } else {
                var expanded = gtrm.expand(keyword, Parameters.EXPANSION_THRESHOLD);

                double bestExpansion = 0;

                for (RankedTerm rankedTerm : expanded) {
                    if (words.contains(rankedTerm.getTerm())) {
                        bestExpansion = Math.max(bestExpansion, rankedTerm.getRank());
                    }
                }

                score += 2 * Math.sqrt(bestExpansion)
                        / subscriberKeywordCount;
            }
        }

        return score >= Parameters.RELEVANCE_THRESHOLD;
    }

}
