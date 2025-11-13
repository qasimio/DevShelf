package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.SearchIndexData;
import java.io.File;
import java.io.IOException;

public class IndexLoader {

    private final String indexFilePath;
    private final ObjectMapper mapper;

    public IndexLoader(String indexFilePath) {
        this.indexFilePath = indexFilePath;
        this.mapper = new ObjectMapper();
    }

    public SearchIndexData loadIndex() {
        System.out.println("Loading pre-compiled index from: " + indexFilePath);
        try {
            File indexFile = new File(indexFilePath);
            if (!indexFile.exists()) {
                // This is a critical error. The app can't run.
                throw new IOException("Index file not found! Please run IndexerMain first.");
            }

            // This one line reads the file and converts it all!
            // Jackson needs the file and the .class of the object it should build.
            SearchIndexData indexData = mapper.readValue(indexFile, SearchIndexData.class);

            System.out.println("Index loaded successfully.");
            return indexData;

        } catch (IOException e) {
            // If we can't load the index, the search engine can't start.
            // We'll throw a RuntimeException to stop the app.
            System.err.println("FATAL ERROR: Could not load index file.");
            e.printStackTrace();
            throw new RuntimeException("Failed to load search index", e);
        }
    }
}
