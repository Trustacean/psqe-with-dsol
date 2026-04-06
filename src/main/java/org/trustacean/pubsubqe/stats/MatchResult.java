package org.trustacean.pubsubqe.stats;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.trustacean.pubsubqe.domain.Subscriber;

public class MatchResult {

    private final Subscriber subscriber;
    private final boolean matches;
    private final boolean isRelevant;
    public static final EventType MATCH_RESULT_EVENT
        = new EventType("MATCH_RESULT_EVENT",
                new MetaData("MATCH_RESULT_EVENT", "broker match result event",
                        new ObjectDescriptor("Text", "Text data", MatchResult.class)));

    public MatchResult(Subscriber subscriber, boolean matches, boolean isRelevant) {
        this.subscriber = subscriber;
        this.matches = matches;
        this.isRelevant = isRelevant;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public boolean isMatches() {
        return matches;
    }

    public boolean isRelevant() {
        return isRelevant;
    }
}
