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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.LoggingService;
import storage.BookLoader;
import storage.IndexLoader;
import ui.gui.controllers.MainViewController;
import ui.gui.services.DevShelfService;
import utils.StopWordLoader;
import utils.TextProcessor;

import java.io.File;
import java.util.*;

public class GuiMain extends Application {

    private static final String BOOKS_RES = "/data/book.json";
    private static final String INDEX_RES = "/data/index_data.json";
    private static final String STOPWORDS_RES = "/data/stopword.txt";

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Starting DevShelf...");

        String appDataPath = utils.StorageUtils.getAppDataDir();
        String logsPath = appDataPath + File.separator + "logs.json";
        String popularityPath = appDataPath + File.separator + "popularity.json";
        System.out.println("User Data Directory: " + appDataPath);

        BookLoader bookLoader = new BookLoader(BOOKS_RES);
        List<Book> books = bookLoader.loadBooks();
        Map<Integer, Book> bookMap = new HashMap<>();
        for(Book b : books) bookMap.put(b.getBookId(), b);

        IndexLoader indexLoader = new IndexLoader(INDEX_RES);
        SearchIndexData indexData = indexLoader.loadIndex();

        Set<String> stopWords = StopWordLoader.loadStopWords(STOPWORDS_RES);
        TextProcessor textProcessor = new TextProcessor(stopWords);

        QueryProcessor queryProcessor = new QueryProcessor(textProcessor,
                indexData.getInvertedIndex(), indexData.getTfIdfVectors(), indexData.getIdfScores());


        LoggingService loggingService = new LoggingService(logsPath);
        ReRanker reRanker = new ReRanker(bookMap, popularityPath);

        System.out.println("Building recommendation graph...");
        Graph graph = new Graph();
        graph.buildGraph(books);
        System.out.println("Graph built with " + graph.adjList.size() + " nodes.");

        List<String> titles = new ArrayList<>();
        for(Book b : books) if(b.getTitle() != null) titles.add(b.getTitle());
        Suggester suggester = new Suggester(titles, stopWords);

        DevShelfService service = new DevShelfService(bookMap, queryProcessor, reRanker, suggester, graph, loggingService);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/gui/fxml/MainView.fxml"));
        Parent root = loader.load();

        MainViewController controller = loader.getController();
        controller.setService(service);

        Scene scene = new Scene(root);
        Image logo = new Image("assets/images/DevShelf6.jpg");
        stage.getIcons().add(logo);
        stage.setTitle("DevShelf - Library Search Engine");
        stage.setScene(scene);
        stage.show();

        System.out.println("✅ GUI Started successfully.");
    }

   @Override
    public void stop() {
       System.out.println("🛑 Application stopping. Running maintenance...");

       // Run analysis in a background thread so the window closes instantly
       new Thread(LogAnalyzerMain::analyze).start();
   }

    public static void main(String[] args) {
        launch(args);
    }
}