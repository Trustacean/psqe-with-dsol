package org.trustacean.pubsubqe.dataset;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.trustacean.pubsubqe.domain.Message;

public class DatasetLoader {

    private static final String PATH = "./src/main/java/org/trustacean/pubsubqe/resources/";
    private static final String DEFAULT_FILE_NAME = "dataset.csv";
    private static final String DEFAULT_CONTEXT_FILE_NAME = "context.csv";

    public static List<Message> load() throws Exception {
        return load(DEFAULT_FILE_NAME, DEFAULT_CONTEXT_FILE_NAME);
    }

    public static List<Message> load(String textDataset, String contextDataset) throws Exception {
        List<Message> list = new ArrayList<>();
        try (BufferedReader brText = new BufferedReader(new java.io.FileReader(PATH + textDataset)); BufferedReader brContext = new BufferedReader(new java.io.FileReader(PATH + contextDataset))) {
            brText.readLine();

            String textLine;
            String contextLine;
            while ((textLine = brText.readLine()) != null && (contextLine = brContext.readLine()) != null) {
                list.add(new Message(contextLine, textLine));
            }
        } catch (Error e) {
            System.out.println(e);
        }
        return list;
    }
}
