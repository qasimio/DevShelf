package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.LogEntry;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LoggingService {

    private final String logFilePath;
    private final ObjectMapper mapper;

    public LoggingService(String logFilePath) {
        this.logFilePath = logFilePath;
        this.mapper = new ObjectMapper();
    }

    public void logClick(String query, int clickedDocId) {
        LogEntry entry = new LogEntry(query, clickedDocId);

        // --- Step 1: Try to create the JSON string ---
        String jsonLogLine;
        try {
            jsonLogLine = mapper.writeValueAsString(entry);
        } catch (IOException e) {
            System.err.println("--- LOGGING SERVICE ERROR (STEP 1: JSON MAPPING) ---");
            System.err.println("Failed to convert LogEntry object to JSON string.");
            e.printStackTrace();
            return; // Stop here if mapping fails
        }

        // --- Step 2: Try to write the string to the file ---
        // Using try-with-resources ensures the writer is closed.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(jsonLogLine);
            writer.newLine();
            writer.flush(); // Force the write to disk *immediately*

        } catch (IOException e) {
            System.err.println("--- LOGGING SERVICE ERROR (STEP 2: FILE WRITE) ---");
            System.err.println("Failed to write log line to: " + logFilePath);
            System.err.println("Check file permissions and if the path is correct.");
            e.printStackTrace();
        }
    }
}