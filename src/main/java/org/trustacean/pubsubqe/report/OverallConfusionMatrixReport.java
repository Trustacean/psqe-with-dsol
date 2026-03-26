package org.trustacean.pubsubqe.report;

import org.djutils.event.Event;
import org.trustacean.pubsubqe.stats.MatchResult;

public class OverallConfusionMatrixReport extends Report {
    private int tp;
    private int fp;
    private int fn;
    private int tn;
    
    public OverallConfusionMatrixReport(String outFileName) {
        super(outFileName);
        tp = 0;
        fp = 0;
        fn = 0;
        tn = 0;
    }

    @Override
    public void notify(Event event) {
        if (event.getType().equals(MatchResult.MATCH_RESULT_EVENT)) {
            MatchResult result = (MatchResult) event.getContent();
            boolean matches = result.isMatches();
            boolean isRelevant = result.isRelevant();

            if (matches && isRelevant) {
                tp++;
            } else if (matches) {
                fp++;
            } else if (isRelevant) {
                fn++;
            } else {
                tn++;
            }
        }
        super.notify(event);
    }

    @Override
    public void done() {
        write("TP,FP,FN,TN");
        write(tp + "," + fp + "," + fn + "," + tn);

        super.done();
    }
}
