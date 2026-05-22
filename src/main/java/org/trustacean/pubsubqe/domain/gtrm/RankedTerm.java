package org.trustacean.pubsubqe.domain.gtrm;

public class RankedTerm {
    private final String term;
    private final double rank;

    public RankedTerm(String term, double r) {
        this.term = term;
        this.rank = r;
    }

    public String getTerm() {
        return term;
    }

    public double getRank() {
        return rank;
    }
}
