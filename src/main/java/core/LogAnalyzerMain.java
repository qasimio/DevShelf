package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.LogEntry;
import utils.StorageUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogAnalyzerMain {

    public static void main(String[] args)  {
        analyze();
    }

    public static void analyze() {

        System.out.println("--- Starting Log Analyzer ---");

        String appDataPath = StorageUtils.getAppDataDir();
        String logsPath = appDataPath + File.separator + "logs.json";
        String popularityOutPath = appDataPath + File.separator + "popularity.json";

        System.out.println("Reading logs from: " + logsPath);

        ObjectMapper mapper = new ObjectMapper();
        Map<Integer, Integer> clickCounts = new HashMap<>();

        File logFile = new File(logsPath);
        if (!logFile.exists()) {
            System.out.println("❌ No logs found at " + logsPath);
            System.out.println("Run the GUI and click some books first!");
            return;
        }


        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                LogEntry entry = mapper.readValue(line, LogEntry.class);
                int docId = entry.getClickedDocId();

                clickCounts.put(docId, clickCounts.getOrDefault(docId, 0) + 1);
                lineCount++;
            }
            System.out.println("Processed " + lineCount + " log entries.");

        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            return;
        }

        Map<Integer, Double> popularityScores = new HashMap<>();
        double maxScore = 0.0;

        for (Integer docId : clickCounts.keySet()) {
            double score = Math.log10(1 + clickCounts.get(docId));
            popularityScores.put(docId, score);
            if (score > maxScore) {
                maxScore = score;
            }
        }

        if (maxScore > 0) {
            for (Integer docId : popularityScores.keySet()) {
                double normalizedScore = popularityScores.get(docId) / maxScore;
                popularityScores.put(docId, normalizedScore);
            }
        }

        try {
            System.out.println("Saving popularity scores to: " + popularityOutPath);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(popularityOutPath), popularityScores);
            System.out.println("--- Log Analyzer Finished ---");
        } catch (IOException e) {
            System.err.println("Error writing popularity file: " + e.getMessage());
        }
    }
}