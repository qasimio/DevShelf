package storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Book;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class BookLoader {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String jsonFilePath;

    public BookLoader(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public List<Book> loadBooks() {

        try {
            File jsonBooks = new File(jsonFilePath);
            return mapper.readValue(jsonBooks, new TypeReference<List<Book>>() {});

        } catch (IOException e) {
            System.err.println("Failed to Load Books from JSON file: " + e.getMessage());
            return Collections.emptyList();
        }

    }

}

// Work on book.json - open and parson book.json