package ui.gui.controllers;

import domain.Book;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink; // <-- NEW
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox; // <-- NEW
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import ui.gui.services.DevShelfService; // <-- NEW

import java.io.IOException;
import java.util.List; // <-- NEW


public class BookDetailController {

    @FXML private ImageView largeCoverImage;
    @FXML private Label fullTitle;
    @FXML private Label authors;
    @FXML private Label category;
    @FXML private Label rating;
    @FXML private Label progLang;
    @FXML private Text descriptionText;
    @Setter
    @FXML private Button readButton;
    @FXML private VBox recommendationsContainer;

    private Book book;
    private DevShelfService service;
    private Stage stage;
    private Scene previousScene;

    // Called by MainViewController to pass data in
    public void setBookData(Book book, DevShelfService service, Stage stage, Scene previousScene) {
        this.book = book;
        this.service = service;
        this.stage = stage;
        this.previousScene = previousScene;

        // 1. Set Text Data
        fullTitle.setText(book.getTitle());
        authors.setText(book.getAuthor());
        category.setText("Category: " + book.getCategory());
        rating.setText(book.getRating() + " ★");
        progLang.setText(book.getProgLang());
        descriptionText.setText(book.getDescription());

        String url = (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty())
                     ? book.getCoverUrl()
                     : "https://via.placeholder.com/150x200?text=No+Cover";
        largeCoverImage.setImage(new Image(url, true));

        // --- 2. Load recommendations ---
        loadRecommendations();
    }

    /**
     * NEW: Asks the service for recommendations and builds the UI.
     */
    private void loadRecommendations() {
        // Clear any old recommendations
        recommendationsContainer.getChildren().clear();

        if (this.service == null || this.book == null) return;

        // Ask the "Brain" for related books
        List<Book> recommendations = service.getRecommendationsFor(this.book);

        if (recommendations.isEmpty()) {
            recommendationsContainer.getChildren().add(new Label("No recommendations found."));
            return;
        }

        // Build a clickable Hyperlink for each recommendation
        for (Book recBook : recommendations) {
            Hyperlink link = new Hyperlink(recBook.getTitle() + " by " + recBook.getAuthor());
            link.setStyle("-fx-font-size: 14px; -fx-text-fill: #2980b9;");

            // --- This is the "reload" logic ---
            link.setOnAction(e -> {
                // "Reloads" this entire page with the new book's data
                setBookData(recBook, this.service, this.stage, this.previousScene);
            });

            recommendationsContainer.getChildren().add(link);
        }
    }

    @FXML
    private void handleBack() {
        // Return to the list view
        if (stage != null && previousScene != null) {
            stage.setScene(previousScene);
        }
    }

    @FXML
    private void handleRead() {
        if (book.getDownLink() == null || book.getDownLink().isEmpty()) {
            System.out.println("❌ No download link for this book.");
            return;
        }

        try {
            System.out.println("📖 Opening book: " + book.getDownLink());

            // Load the Web Viewer
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/gui/fxml/WebViewWindow.fxml"));
            Parent root = loader.load();

            // Pass the URL to the controller
            WebViewController controller = loader.getController();
            controller.loadUrl(book.getDownLink());

            // Open in new Max Window
            Stage webStage = new Stage();
            webStage.setTitle("Reading: " + book.getTitle());
            webStage.setScene(new Scene(root));
            webStage.initModality(Modality.APPLICATION_MODAL); // Block background interaction
            webStage.setMaximized(true);
            webStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}