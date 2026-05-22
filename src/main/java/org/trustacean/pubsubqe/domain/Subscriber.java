package org.trustacean.pubsubqe.domain;

import java.util.Arrays;
import java.util.HashSet;

import org.trustacean.pubsubqe.domain.gtrm.GlobalTermRelationshipModel;

public class Subscriber {

    private String topic;
    private HashSet<String> keywords;

    public Subscriber(String... keywords) {
        init(keywords);
    }

    private void init(String... keywords) {
        this.topic = "";
        for (String keyword : keywords) {
            this.topic = this.topic + keyword + " ";
        }

        this.topic = this.topic.trim();
        this.keywords = new HashSet<>(Arrays.asList(GlobalTermRelationshipModel.normalize(this.getTopic())));
    }

    public void setKeywords(String[] keywords) {
        this.keywords.addAll(Arrays.asList(keywords));
    }

    public HashSet<String> getKeywords() {
        return this.keywords;
    }

    public String getTopic() {
        return this.topic;
    }

    public void receive(Message msg) {
        String keystr = "";
        for (String keyword : keywords) {
            keystr = keystr + keyword + " ";
        }
        // System.out.println("Subscriber[" + keystr + "] received: " + msg.getText());
    }
}
