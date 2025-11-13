package core;

import domain.Book;
import domain.SearchIndexData;
import features.recommendation.Graph;
import features.search.QueryProcessor;
import features.search.ReRanker;
import features.search.Suggester;
import utils.LoggingService;
import storage.BookLoader;
import storage.IndexLoader;
import ui.cli.CliView;
import utils.StopWordLoader;
import utils.TextProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    // --- All paths are now in one place ---
    private static final String INDEX_FILE_PATH = "src/main/resources/data/index_data.json";
    private static final String STOPWORD_FILE_PATH = "src/main/resources/data/stopword.txt";
    private static final String BOOKS_FILE_PATH = "src/main/resources/data/book.json";
    private static final String LOGS_FILE_PATH = "src/main/resources/logs/logs.json";
    private static final String POPULARITY_FILE_PATH = "src/main/resources/logs/popularity.json";

    public static void main(String[] args) {
        System.out.println("📖 Assembling DevShelf Engine...");

        // --- 1. Load All Data ---
        IndexLoader loader = new IndexLoader(INDEX_FILE_PATH);
        SearchIndexData loadedData = loader.loadIndex();

        BookLoader bookLoader = new BookLoader(BOOKS_FILE_PATH);
        List<Book> allBooks = bookLoader.loadBooks();
        Map<Integer, Book> bookMap = new HashMap<>();
        for (Book b : allBooks) {
            if (b != null) bookMap.put(b.getBookId(), b);
        }

        // --- 2. Build All Services ---
        Set<String> stopWords = StopWordLoader.loadStopWords(STOPWORD_FILE_PATH);
        TextProcessor textProcessor = new TextProcessor(stopWords);

        QueryProcessor queryProcessor = new QueryProcessor(textProcessor, loadedData.getInvertedIndex(),
                loadedData.getTfIdfVectors(), loadedData.getIdfScores());

        LoggingService loggingService = new LoggingService(LOGS_FILE_PATH);
        ReRanker reRanker = new ReRanker(bookMap, POPULARITY_FILE_PATH);

        Graph graph = new Graph();
        graph.buildGraph(allBooks);

        List<String> allTitles = new ArrayList<>();
        for (Book b : allBooks) if (b.getTitle() != null) allTitles.add(b.getTitle());
        Suggester suggester = new Suggester(allTitles, stopWords);

        // --- 3. Build The UI ---
        CliView view = new CliView();

        // --- 4. Assemble the Controller & Start ---
        BookSearchEngine engine = new BookSearchEngine(
                bookMap, queryProcessor, reRanker, suggester,
                graph, loggingService, view
        );

        System.out.println("...Assembly complete. Starting application.");
        engine.run(); // Start the main loop
    }
}
