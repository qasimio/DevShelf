package storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Book;

import java.io.File;

import utils.StorageUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class BookLoader {
    private final String resourcePath; // e.g., "/data/book.json"

    public BookLoader(String resourcePath) {
        this.resourcePath = resourcePath;
    }


    public List<Book> loadBooks() {
        ObjectMapper mapper = new ObjectMapper();

        // 1. Try Loading from AppData (The Update)
        File updatedFile = new File(StorageUtils.getAppDataDir(), "book.json");
        if (updatedFile.exists()) {
            try {
                System.out.println("📂 Loading books from local update...");
                return mapper.readValue(updatedFile, new TypeReference<List<Book>>() {});
            } catch (Exception e) {
                e.printStackTrace(); // Fallback if corrupt
            }
        }

        // 2. Fallback to JAR (Factory Default)
        System.out.println("📦 Loading factory default books...");
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            return mapper.readValue(inputStream, new TypeReference<List<Book>>() {});
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

}
// Work on book.json - open and parson book.json