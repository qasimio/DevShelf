package core;

import domain.Book;
import domain.SearchIndexData;
import features.recommendation.Graph;
import features.search.QueryProcessor;
import features.search.ReRanker;
import features.search.Suggester;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.LoggingService;
import storage.BookLoader;
import storage.IndexLoader;
import ui.gui.controllers.MainViewController;
import ui.gui.services.DevShelfService;
import utils.StopWordLoader;
import utils.TextProcessor;

import java.util.*;

public class GuiMain extends Application {

    // Define paths constants (adjust if yours differ)
    private static final String BOOKS_FILE = "src/main/resources/data/book.json";
    private static final String INDEX_FILE = "src/main/resources/data/index_data.json";
    private static final String STOPWORDS_FILE = "src/main/resources/data/stopword.txt";
    private static final String LOGS_FILE = "src/main/resources/logs/logs.json";
    private static final String POPULARITY_FILE = "src/main/resources/logs/popularity.json";

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("🚀 Starting DevShelf GUI...");

        // --- 1. Load Backend Services (Exact same as CLI) ---
        // (We do this here so the UI has data ready immediately)
        BookLoader bookLoader = new BookLoader(BOOKS_FILE);
        List<Book> books = bookLoader.loadBooks();
        Map<Integer, Book> bookMap = new HashMap<>();
        for(Book b : books) bookMap.put(b.getBookId(), b);

        IndexLoader indexLoader = new IndexLoader(INDEX_FILE);
        SearchIndexData indexData = indexLoader.loadIndex();

        Set<String> stopWords = StopWordLoader.loadStopWords(STOPWORDS_FILE);
        TextProcessor textProcessor = new TextProcessor(stopWords);

        QueryProcessor queryProcessor = new QueryProcessor(textProcessor,
                indexData.getInvertedIndex(), indexData.getTfIdfVectors(), indexData.getIdfScores());

        LoggingService loggingService = new LoggingService(LOGS_FILE);
        ReRanker reRanker = new ReRanker(bookMap, POPULARITY_FILE);

        List<String> titles = new ArrayList<>();
        for(Book b : books) if(b.getTitle() != null) titles.add(b.getTitle());
        Suggester suggester = new Suggester(titles, stopWords);

        // --- 2. Create the App Brain ---
        DevShelfService service = new DevShelfService(bookMap, queryProcessor, reRanker, suggester, loggingService);

        // --- 3. Load the UI ---
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/gui/fxml/MainView.fxml"));
        Parent root = loader.load();

        // --- 4. Inject the Brain into the Controller ---
        MainViewController controller = loader.getController();
        controller.setService(service);

        // --- 5. Show Window ---
        Scene scene = new Scene(root);
        stage.setTitle("DevShelf - Library Search Engine");
        stage.setScene(scene);
        stage.show();

        System.out.println("✅ GUI Started successfully.");
    }
    public static void main(String[] args) {
        launch(args);
    }
}