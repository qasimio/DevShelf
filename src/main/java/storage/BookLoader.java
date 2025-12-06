package storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Book;

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

        // 🚀 INDUSTRY STANDARD: Load from Classpath (Inside the JAR)
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("CRITICAL: Could not find resource: " + resourcePath);
                return Collections.emptyList();
            }
            return mapper.readValue(inputStream, new TypeReference<List<Book>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
// Work on book.json - open and parson book.json