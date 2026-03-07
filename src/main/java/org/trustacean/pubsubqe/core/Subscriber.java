/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.trustacean.pubsubqe.core;

public class Subscriber {
    private String[] keywords;

    public Subscriber(String... keywords) {
        this.keywords = keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public boolean matches(Message msg) {
        String text = msg.getText().toLowerCase();
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void recieve(Message msg) {
        String keystr = "";
        for (String keyword : keywords) {
            keystr = keystr + keyword + " ";
        }
        System.out.println("Subscriber[" + keystr + "] recieved: " + msg.getText());
    }
}