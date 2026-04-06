package org.trustacean.pubsubqe.report;

import java.util.HashMap;
import java.util.Map;

import org.djutils.event.Event;
import org.trustacean.pubsubqe.domain.Message;

public class EventContextCountReport extends Report {
    private final Map<String, Integer> contextCounts;

    public EventContextCountReport(String outFileName) {
        super(outFileName);
        this.contextCounts = new HashMap<>();
    }

    @Override
    public void notify(Event event) {
        if (event.getType().equals(Message.MESSAGE_PUBLISHED_EVENT)) {
            Message msg = (Message) event.getContent();

            String context = msg.getContext();
            contextCounts.put(context, contextCounts.getOrDefault(context, 0) + 1);
        }
        super.notify(event);
    }

    @Override
    public void done() {
        write("context,count");
        for (Map.Entry<String, Integer> entry : contextCounts.entrySet()) {
            write(entry.getKey() + "," + entry.getValue());
        }
        
        super.done();
    }
}
