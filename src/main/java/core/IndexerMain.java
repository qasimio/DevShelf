package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import domain.Book;
import domain.Posting;
import domain.SearchIndexData;
import features.search.IndexBuilder;
import storage.BookLoader;
import utils.StopWordLoader;
import utils.TextProcessor;
import utils.TfIdfCalculator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndexerMain {

    // OUTPUT: We write to the source folder so Maven includes it in the build
    private static final String INDEX_OUTPUT_PATH = "src/main/resources/data/index_data.json";

    // INPUTS: We use Resource Paths (starts with /) because Loaders use getResourceAsStream
    private static final String STOPWORD_RES = "/data/stopword.txt";
    private static final String BOOK_RES = "/data/book.json";

    public static void main(String[] args) {
        System.out.println("--- Starting Offline Indexer ---");
        try {
            // 1. Prepare Output File
            File outputFile = new File(INDEX_OUTPUT_PATH);
            // Ensure the directory exists
            outputFile.getParentFile().mkdirs();

            // 2. Load Data using updated Loaders
            System.out.println("Loading stopwords from resource: " + STOPWORD_RES);
            Set<String> stopWords = StopWordLoader.loadStopWords(STOPWORD_RES);
            TextProcessor textProcessor = new TextProcessor(stopWords);

            System.out.println("Loading books from resource: " + BOOK_RES);
            BookLoader loader = new BookLoader(BOOK_RES);
            IndexBuilder indexer = new IndexBuilder(textProcessor);
            TfIdfCalculator tfIdfCalculator = new TfIdfCalculator();

            // 3. Indexing Process
            System.out.println("Indexing books...");
            List<Book> allBooks = loader.loadBooksFromSource(BOOK_RES);
            if (allBooks.isEmpty()) {
                System.err.println("❌ Critical Error: No books loaded. Check book.json path.");
                return;
            }

            for(Book book : allBooks) {
                indexer.indexDocument(book);
            }
            Map<String, List<Posting>> invertedIndex = indexer.getInvertedIndex();
            System.out.println("Indexing Complete. Found " + invertedIndex.size() + " unique terms.");

            // 4. Calculations
            System.out.println("Calculating TF-IDF vectors...");
            tfIdfCalculator.calculateIdf(invertedIndex, allBooks.size());
            tfIdfCalculator.calculateTfIdf(invertedIndex);

            // 5. Save Data
            SearchIndexData indexData = new SearchIndexData(
                    invertedIndex,
                    tfIdfCalculator.getTfIdfVectors(),
                    tfIdfCalculator.getIdfScores()
            );

            System.out.println("Saving index to: " + outputFile.getAbsolutePath());
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(outputFile, indexData);

            System.out.println("--- ✅ Indexer Finished Successfully! --- ");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" --- ❌ Indexer failed with an error. --- ");
        }
    }
}

