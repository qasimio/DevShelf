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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndexerMain {

    private static final String INDEX_FILE_PATH = ("src/main/resources/data/index_data.json");

    public static void main(String[] args) {
        System.out.println("--- Starting Offline Indexer ---");
        try {

            Set<String> stopWords = StopWordLoader.loadStopWords("src/main/resources/data/stopword.txt");
            TextProcessor textProcessor = new TextProcessor(stopWords);

            BookLoader loader = new BookLoader("src/main/resources/data/book.json");
            IndexBuilder indexer = new IndexBuilder(textProcessor);
            TfIdfCalculator tfIdfCalculator = new TfIdfCalculator();


            System.out.println("Loading and indexing books...");
            List<Book> allBooks = loader.loadBooks();
            for(Book book : allBooks) {
                indexer.indexDocument(book);
            }
            Map<String, List<Posting>> invertedIndex = indexer.getInvertedIndex();
            System.out.println("Indexing Complete. Found " + invertedIndex.size() + " terms");


            System.out.println("Calculating TF-IDF vectors...");
            tfIdfCalculator.calculateIdf(invertedIndex, allBooks.size());
            tfIdfCalculator.calculateTfIdf(invertedIndex);
            Map<Integer, Map<String, Double>> tfIdfVectors = tfIdfCalculator.getTfIdfVectors();
            Map<String, Double> idfScores = tfIdfCalculator.getIdfScores();
            System.out.println("TF-IDF calculation complete.");

            System.out.println("Saving index to file: " + INDEX_FILE_PATH);
            SearchIndexData indexData = new SearchIndexData(invertedIndex, tfIdfVectors, idfScores);

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(INDEX_FILE_PATH), indexData);

            System.out.println("--- Indexer Finished Successfully! --- ");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" --- Indexer failed with an error. --- ");
        }
    }

}