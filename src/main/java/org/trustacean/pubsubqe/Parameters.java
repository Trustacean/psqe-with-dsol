package org.trustacean.pubsubqe;

public class Parameters {
    public static final MatchingStrategy MATCHING_STRATEGY = MatchingStrategy.OR_MATCH;
    public static final int TOP_K = 0;
    public static final int WINDOW_SIZE = 5000;
    public static final int RECOMPUTE_INTERVAL = 50000;
    public static final double EXPANSION_THRESHOLD = 0.04;
    public static final double RELEVANCE_THRESHOLD = 0.4;

    public enum MatchingStrategy {
        AND_MATCH,
        OR_MATCH,
        AND_EXPANSION,
        OR_EXPANSION,
        RELEVANCY_SCORE,
    }
}
