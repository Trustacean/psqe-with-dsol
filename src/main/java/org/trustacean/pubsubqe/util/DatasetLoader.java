package org.trustacean.pubsubqe.util;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.trustacean.pubsubqe.core.Message;

public class DatasetLoader {

    private static final String DEFAULT_FILE_NAME = "src\\main\\java\\org\\trustacean\\pubsubqe\\input\\dataset.csv";
    // private static final String DEFAULT_CONTEXT_FILE_NAME = "../input/context.csv";

    public static List<Message> load() throws Exception {
        return load(DEFAULT_FILE_NAME);
    }

    public static List<Message> load(String filename) throws Exception {
        List<Message> list = new ArrayList<>();

        File file = new File(filename);
        if (file.exists()) {
            System.out.println("File Exists");
        } else {
            System.out.println("File does not Exist.");
        }

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filename))) {
            br.readLine();
            
            String line;
            while ((line = br.readLine()) != null) {
                list.add(new Message("", line));
            }
        }

        return list;
    }
}
