package org.trustacean.pubsubqe.report;

import java.util.Map;
import java.util.Set;

import org.trustacean.pubsubqe.domain.gtrm.GlobalTermRelationshipModel;
import org.trustacean.pubsubqe.domain.gtrm.RankedTerm;

public class RelationshipReport extends Report {
    private final GlobalTermRelationshipModel gtrm;

    public RelationshipReport(String outFileName, GlobalTermRelationshipModel gtrm) {
        super(outFileName);
        this.gtrm = gtrm;
    }

    @Override
    public void done() {
        Map<String, Set<RankedTerm>> relationships = gtrm.getTermRelationships();

        write("{");

        int i = 0;
        for (Map.Entry<String, Set<RankedTerm>> entry : relationships.entrySet()) {
            String term = entry.getKey();
            Set<RankedTerm> relatedTerms = entry.getValue();

            write("\"" + term + "\": [");

            int j = 0;
            for (RankedTerm rt : relatedTerms) {
                String obj = String.format(
                        "{\"term\": \"%s\", \"rank\": %f}",
                        rt.getTerm(), rt.getRank());

                write(obj + (j++ < relatedTerms.size() - 1 ? "," : ""));
            }

            write("]" + (i++ < relationships.size() - 1 ? "," : ""));
        }

        write("}");
    }

}
