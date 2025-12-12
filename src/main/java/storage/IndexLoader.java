package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.SearchIndexData;
import utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class IndexLoader {
    private final String resourcePath;

    public IndexLoader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public SearchIndexData loadIndex() {
        ObjectMapper mapper = new ObjectMapper();

        // 1️⃣ Try loading from AppData (Updated version)
        File updatedFile = new File(StorageUtils.getAppDataDir(), "index.json");
        if (updatedFile.exists()) {
            try {
                System.out.println("📂 Loading index from local update...");
                return mapper.readValue(updatedFile, SearchIndexData.class);
            } catch (IOException e) {
                e.printStackTrace(); // Fallback if file is corrupt
            }
        }

        // 2️⃣ Fallback to classpath resource (Factory default)
        System.out.println("📦 Loading factory default index...");
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            return mapper.readValue(inputStream, SearchIndexData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Or throw RuntimeException depending on your preference
        }
    }
}
