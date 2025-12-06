package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class StopWordLoader {
    public static Set<String> loadStopWords(String resourcePath) {
        Set<String> stopWords = new HashSet<>();
        // 🚀 INDUSTRY STANDARD: Load from Classpath
        try (InputStream inputStream = StopWordLoader.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) return stopWords;

            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stopWords;
    }
}