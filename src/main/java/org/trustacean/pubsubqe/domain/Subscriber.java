package org.trustacean.pubsubqe.domain;

public class Subscriber {

    private String[] keywords;
    private String[] postProcessedKeywords;

    public Subscriber(String... keywords) {
        this.keywords = keywords;
        this.postProcessedKeywords = new String[keywords.length];
        for (int i = 0; i < keywords.length; i++) {
            this.postProcessedKeywords[i] = keywords[i].toLowerCase();
        }
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
        setPostProcessedKeywords(keywords);
    }

    public void setPostProcessedKeywords(String[] postProcessedKeywords) {
        this.postProcessedKeywords = postProcessedKeywords;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public String getKeywordStr() {
        String keystr = "";
        for (String keyword : keywords) {
            keystr = keystr + keyword + " ";
        }

        keystr = keystr.trim();
        return keystr;
    }

    public String[] getPostProcessedKeywords() {
        return postProcessedKeywords;
    }

    public boolean matches(Message msg) {
        String text = msg.getText().toLowerCase();
        for (String keyword : postProcessedKeywords) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void receive(Message msg) {
        String keystr = "";
        for (String keyword : keywords) {
            keystr = keystr + keyword + " ";
        }
        // System.out.println("Subscriber[" + keystr + "] received: " + msg.getText());
    }
}
