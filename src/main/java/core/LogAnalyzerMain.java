package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.LogEntry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogAnalyzerMain {

    private static final String LOGS_FILE_PATH = "src/main/resources/logs/logs.json";
    private static final String POPULARITY_OUT_PATH = "src/main/resources/logs/popularity.json";

    public static void main(String[] args) {
        System.out.println("--- Starting Log Analyzer ---");
        ObjectMapper mapper = new ObjectMapper();

        // This map will store: <DocID, ClickCount>
        Map<Integer, Integer> clickCounts = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(LOGS_FILE_PATH))) {
            String line;
            int lineCount = 0;
            // Read the JSONL file line by line
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                LogEntry entry = mapper.readValue(line, LogEntry.class);
                int docId = entry.getClickedDocId();

                // Increment the count for this DocID
                clickCounts.put(docId, clickCounts.getOrDefault(docId, 0) + 1);
                lineCount++;
            }
            System.out.println("Processed " + lineCount + " log entries.");

        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            return;
        }

        // --- Now, we normalize the scores ---
        // We'll use a simple log(1 + clicks) to "dampen" the score
        // so 1000 clicks isn't 1000x better than 1 click.

        Map<Integer, Double> popularityScores = new HashMap<>();
        double maxScore = 0.0;

        for (Integer docId : clickCounts.keySet()) {
            double score = Math.log10(1 + clickCounts.get(docId));
            popularityScores.put(docId, score);
            if (score > maxScore) {
                maxScore = score; // Find the max score
            }
        }

        // --- Normalize all scores from 0 to 1 ---
        if (maxScore > 0) {
            for (Integer docId : popularityScores.keySet()) {
                double normalizedScore = popularityScores.get(docId) / maxScore;
                popularityScores.put(docId, normalizedScore);
            }
        }

        // --- Save the final popularity map ---
        try {
            System.out.println("Saving popularity scores to: " + POPULARITY_OUT_PATH);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(POPULARITY_OUT_PATH), popularityScores);
            System.out.println("--- Log Analyzer Finished ---");
        } catch (IOException e) {
            System.err.println("Error writing popularity file: " + e.getMessage());
        }
    }
}