package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.SearchIndexData;
import java.io.IOException;
import java.io.InputStream;

public class IndexLoader {
    private final String resourcePath;

    public IndexLoader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public SearchIndexData loadIndex() {
        ObjectMapper mapper = new ObjectMapper();

        // 🚀 INDUSTRY STANDARD: Load from Classpath
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            return mapper.readValue(inputStream, SearchIndexData.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load index", e);
        }
    }
}