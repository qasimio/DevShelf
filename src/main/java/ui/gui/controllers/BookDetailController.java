package ui.gui.controllers;

import domain.Book;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class BookDetailController {

    @FXML private ImageView largeCoverImage;
    @FXML private Label fullTitle;
    @FXML private Label authors;
    @FXML private Label category;
    @FXML private Label rating;
    @FXML private Label progLang;
    @FXML private Text descriptionText;
    @FXML private Button readButton;

    private Book book;
    private Stage stage;
    private Scene previousScene;

    // Called by MainViewController to pass data in
    public void setBookData(Book book, Stage stage, Scene previousScene) {
        this.book = book;
        this.stage = stage;
        this.previousScene = previousScene;

        // 1. Set Text Data
        fullTitle.setText(book.getTitle());
        authors.setText(book.getAuthor());
        category.setText(book.getCategory());
        rating.setText(book.getRating() + " ★");
        progLang.setText(book.getProgLang());
        descriptionText.setText(book.getDescription());

        // 2. Set Image (Async load)
        String url = (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty())
                ? book.getCoverUrl()
                : "https://via.placeholder.com/150x200?text=No+Cover";

        // 'true' means load in background to prevent freezing
        largeCoverImage.setImage(new Image(url, true));
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