package ui.gui.controllers;

import domain.Book;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Setter;
import ui.gui.services.DevShelfService;
import ui.gui.services.DevShelfService.SearchResponse;

import java.io.IOException;

public class MainViewController {

    @FXML private TextField searchField;
    @FXML private VBox resultsContainer;
    @FXML private Label statusLabel;

    // Called by GuiMain to inject the service
    @Setter
    private DevShelfService service;

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) return;

        resultsContainer.getChildren().clear();
        statusLabel.setText("Searching...");

        // 1. Call the Service
        SearchResponse response = service.search(query);

        // 2. Update UI based on response
        if (response.books.isEmpty()) {
            statusLabel.setText("❌ No results found for \"" + query + "\"");
        } else {
            if (response.isSuggestion) {
                statusLabel.setText("💡 No results for \"" + query + "\". Showing results for \"" + response.successfulQuery + "\"");
            } else {
                statusLabel.setText("✅ Found " + response.books.size() + " books for \"" + query + "\"");
            }

            // 3. Render the cards
            displayBooks(response.books);
        }
    }

    private void displayBooks(java.util.List<Book> books) {
        for (Book book : books) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/gui/fxml/BookCard.fxml"));
                Node card = loader.load();

                BookCardController cardController = loader.getController();
                cardController.setData(book);

                // Add click listener
                card.setOnMouseClicked(e -> {
                    System.out.println("User clicked: " + book.getTitle());
                    service.logClick(searchField.getText(), book.getBookId());
                });

                resultsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}