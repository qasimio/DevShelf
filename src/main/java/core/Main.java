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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    private static final String BOOKS_RES = "/data/book.json";
    private static final String INDEX_RES = "/data/index_data.json";
    private static final String STOPWORDS_RES = "/data/stopword.txt";

    public static void main(String[] args) {
        System.out.println("Assembling DevShelf Engine...");

        String appDataPath = utils.StorageUtils.getAppDataDir();
        String logsPath = appDataPath + File.separator + "logs.json";
        String popularityPath = appDataPath + File.separator + "popularity.json";
        System.out.println("User Data Directory: " + appDataPath);

        IndexLoader loader = new IndexLoader(INDEX_RES);
        SearchIndexData loadedData = loader.loadIndex();

        BookLoader bookLoader = new BookLoader(BOOKS_RES);
        List<Book> allBooks = bookLoader.loadBooks();
        Map<Integer, Book> bookMap = new HashMap<>();
        for (Book b : allBooks) {
            if (b != null) bookMap.put(b.getBookId(), b);
        }

        Set<String> stopWords = StopWordLoader.loadStopWords(STOPWORDS_RES);
        TextProcessor textProcessor = new TextProcessor(stopWords);

        QueryProcessor queryProcessor = new QueryProcessor(
                textProcessor,
                loadedData.getInvertedIndex(),
                loadedData.getTfIdfVectors(),
                loadedData.getIdfScores()
        );

        LoggingService loggingService = new LoggingService(logsPath);
        ReRanker reRanker = new ReRanker(bookMap, popularityPath);

        Graph graph = new Graph();
        graph.buildGraph(allBooks);

        List<String> allTitles = new ArrayList<>();
        for (Book b : allBooks) if (b.getTitle() != null) allTitles.add(b.getTitle());
        Suggester suggester = new Suggester(allTitles, stopWords);

        CliView view = new CliView();

        BookSearchEngine engine = new BookSearchEngine(
                bookMap, queryProcessor, reRanker, suggester,
                graph, loggingService, view
        );

        System.out.println("...Assembly complete. Starting application.");
        engine.run();
    }
}
