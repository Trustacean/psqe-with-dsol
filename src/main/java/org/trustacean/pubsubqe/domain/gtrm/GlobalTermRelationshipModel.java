package org.trustacean.pubsubqe.domain.gtrm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.trustacean.pubsubqe.Parameters;
import org.trustacean.pubsubqe.domain.Message;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class GlobalTermRelationshipModel implements EventListener {

    // CONFIG
    private static final int WINDOW_SIZE = Parameters.WINDOW_SIZE;
    private static final int RECOMPUTE_INTERVAL = Parameters.RECOMPUTE_INTERVAL;
    private static final int TOP_K = Parameters.TOP_K;

    private final Set<String> queryTerms = new HashSet<>();

    // STATE
    private final Deque<String[]> messageWindow = new ArrayDeque<>();
    private final Map<String, Set<RankedTerm>> termRelationships = new HashMap<>();

    private int messageCount = 0;

    // NLP PIPELINE
    private static final Set<String> STOP_WORDS = Set.of(
            "the", "is", "at", "which", "on", "and", "a", "of", "to", "in",
            "for", "you", "your", "yours", "me", "my", "we", "our", "they",
            "be", "been", "was", "were", "am", "are",
            "'s", "''", "``", "'", "-", "--",
            "@url", "@user", "@names", "rt",
            ".", ",", ":", ";", "!", "?", "(", ")");

    private static final ThreadLocal<StanfordCoreNLP> PIPELINE = ThreadLocal.withInitial(() -> {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,pos,lemma");
        return new StanfordCoreNLP(props);
    });

    public GlobalTermRelationshipModel() {
        super();
    }

    public void addQueryTerm(String term) {
        this.queryTerms.add(term);
    }

    @Override
    public void notify(Event event) {
        Message msg = getEventMessage(event);
        if (msg == null)
            return;

        String[] terms = normalize(msg.getText());
        addToWindow(terms);

        if (++this.messageCount % RECOMPUTE_INTERVAL == 0) {
            recomputeModel();
        }
    }

    private Message getEventMessage(Event event) {
        if (event.getType().equals(Message.MESSAGE_PUBLISHED_EVENT)) {
            return (Message) event.getContent();
        }
        return null;
    }

    // ===== CORE PIPELINE =====

    private void recomputeModel() {
        System.out.println("Recomputing GTRM...");
        System.out.println("Message count: " + this.messageCount);

        Map<String, List<String[]>> virtualDocs = buildVirtualDocuments();

        Map<String, Double> pq = computeQuerySpecificity(virtualDocs);

        Map<String, Map<String, Double>> g = computeTermStrength(virtualDocs);

        Map<String, Integer> pw = computeTermQueryLinks(g);

        // Step 5 + 6
        updateRelationships(g, pq, pw);
    }

    // ===== STEP 1: VIRTUAL DOCS =====
    /*
     */
    private Map<String, List<String[]>> buildVirtualDocuments() {
        Map<String, List<String[]>> virtualDocs = new HashMap<>();

        for (String q : this.queryTerms) {
            virtualDocs.put(q, new ArrayList<>());
        }

        for (String[] msg : this.messageWindow) {
            for (String q : this.queryTerms) {
                if (contains(msg, q)) {
                    virtualDocs.get(q).add(msg);
                }
            }
        }

        return virtualDocs;
    }

    // ===== STEP 2: QUERY SPECIFICITY =====
    private Map<String, Double> computeQuerySpecificity(Map<String, List<String[]>> virtualDocs) {
        Map<String, Double> pq = new HashMap<>();
        int k = this.messageWindow.size();

        for (String q : this.queryTerms) {
            int size = virtualDocs.get(q).size();
            pq.put(q, k == 0 ? 0 : (k - size) / (double) k);
        }

        return pq;
    }

    // ===== STEP 3: TERM STRENGTH g(w|q) =====
    private Map<String, Map<String, Double>> computeTermStrength(Map<String, List<String[]>> virtualDocs) {
        Map<String, Map<String, Double>> g = new HashMap<>();

        for (String q : this.queryTerms) {
            Map<String, Integer> tf = new HashMap<>();
            List<String[]> docs = virtualDocs.get(q);

            for (String[] doc : docs) {
                for (String term : doc) {
                    tf.merge(term, 1, Integer::sum);
                }
            }

            int size = docs.size();
            Map<String, Double> weights = new HashMap<>();

            for (var entry : tf.entrySet()) {
                weights.put(entry.getKey(), size == 0 ? 0 : entry.getValue() / (double) size);
            }

            g.put(q, weights);
        }

        return g;
    }

    // ===== STEP 4: Pw(w) =====
    private Map<String, Integer> computeTermQueryLinks(Map<String, Map<String, Double>> g) {
        Map<String, Integer> pw = new HashMap<>();

        for (String q : this.queryTerms) {
            for (String term : g.get(q).keySet()) {
                pw.merge(term, 1, Integer::sum);
            }
        }

        return pw;
    }

    // ===== STEP 5 & 6: FINAL SCORE & STORE RESULTS =====

    private void updateRelationships(
            Map<String, Map<String, Double>> g,
            Map<String, Double> pq,
            Map<String, Integer> pw) {

        termRelationships.clear();

        for (String q : this.queryTerms) {

            Map<String, Double> localScores = new HashMap<>();

            for (Map.Entry<String, Double> entry : g.get(q).entrySet()) {
                String term = entry.getKey();

                if (term.equals(q))
                    continue;

                double gwq = entry.getValue();
                double specificity = pq.getOrDefault(q, 0.0);
                int sharedQueries = pw.getOrDefault(term, 1);

                // Query-specific ranking
                double score = (gwq * specificity) / sharedQueries;

                localScores.put(term, score);
            }

            Set<RankedTerm> ranked = localScores.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .limit(TOP_K)
                    .map(e -> new RankedTerm(e.getKey(), e.getValue()))
                    .collect(Collectors.toSet());

            termRelationships.put(q, ranked);
        }

    }

    // ===== WINDOW MANAGEMENT =====

    private void addToWindow(String[] terms) {
        messageWindow.addLast(terms);

        if (messageWindow.size() > WINDOW_SIZE) {
            messageWindow.removeFirst();
        }
    }

    // ===== QUERY EXPANSION =====

    public Set<RankedTerm> expand(String term, double threshold) {
        return termRelationships.getOrDefault(term, Set.of()).stream()
                .filter(r -> r.getRank() >= threshold)
                .collect(Collectors.toSet());
    }

    private boolean contains(String[] arr, String target) {
        for (String s : arr) {
            if (s.equals(target))
                return true;
        }
        return false;
    }

    public Map<String, Set<RankedTerm>> getTermRelationships() {
        return this.termRelationships;
    }

    public static String[] normalize(String text) {
        CoreDocument doc = new CoreDocument(text);
        PIPELINE.get().annotate(doc);

        return doc.tokens().stream()
                .map(token -> token.lemma().toLowerCase())
                .filter(word -> !STOP_WORDS.contains(word))
                .filter(word -> word.matches("[a-z]+"))
                .toArray(String[]::new);
    }
}
