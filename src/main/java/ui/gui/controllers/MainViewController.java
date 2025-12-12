package ui.gui.controllers;

import domain.Book;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.geometry.Side;
import lombok.Setter;
import ui.gui.services.DevShelfService;
import ui.gui.services.DevShelfService.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewController {

    @FXML private TextField searchField;
    @FXML private VBox resultsContainer;
    @FXML private Label statusLabel;

    @FXML private ComboBox<String> sortCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ContextMenu suggestionsPopup = new ContextMenu();

    @Setter
    private DevShelfService service;

    private List<Book> originalResults = new ArrayList<>();
    private List<Book> currentDisplayList = new ArrayList<>();

    @FXML
    public void initialize() {
        sortCombo.setItems(FXCollections.observableArrayList(
                "Relevance", "Rating: High to Low", "Title: A-Z"
        ));
        sortCombo.getSelectionModel().selectFirst();
        sortCombo.setOnAction(e -> applySortAndFilter());
        categoryCombo.setOnAction(e -> applySortAndFilter());

        javafx.application.Platform.runLater(() -> {
            if (service != null) loadTrending();
        });

        setupAutocomplete();
    }

    private void setupAutocomplete() {
        suggestionsPopup.setAutoHide(true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            List<String> suggestions = service.getAutoCompletions(newValue);
            if (suggestions.isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            suggestionsPopup.getItems().clear();
            for (String suggestion : suggestions) {
                MenuItem item = new MenuItem(suggestion);
                item.setOnAction(e -> {
                    searchField.setText(suggestion);
                    suggestionsPopup.hide();
                    handleSearch(); // Trigger search on click
                });
                suggestionsPopup.getItems().add(item);
            }

            if (!suggestionsPopup.isShowing()) {
                suggestionsPopup.show(searchField, Side.BOTTOM, 0, 0);
            }
        });

    }

    @FXML
    private void handleHome() {
        searchField.clear();
        loadTrending();
        sortCombo.getSelectionModel().selectFirst();
        categoryCombo.getSelectionModel().select("All Categories");
    }

    private void loadTrending() {
        statusLabel.setText("🔥 Trending Books - Top Picks by Users");
        List<Book> trending = service.getTrendingBooks();

        this.originalResults = trending;
        this.currentDisplayList = trending;

        populateCategoryDropdown(trending);
        displayBooks(trending);
    }


    @FXML
    private void handleSearch() {
        suggestionsPopup.hide();

        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) {
            handleHome();
            return;
        }

        statusLabel.setText("Searching for \"" + query + "\"...");
        resultsContainer.getChildren().clear(); // Clear old results

        SearchResponse response = service.search(query);

        this.originalResults = response.books;
        this.currentDisplayList = new ArrayList<>(this.originalResults);

        if (this.originalResults.isEmpty()) {
            if (response.isSuggestion) {
                statusLabel.setText("❌ No results found for \"" + query + "\". Also tried \"" + response.successfulQuery + "\".");
            } else {
                statusLabel.setText("❌ No results found for \"" + query + "\".");
            }
            populateCategoryDropdown(this.originalResults); // Clear dropdowns
        } else {
            if (response.isSuggestion) {
                statusLabel.setText("💡 No results for \"" + query + "\". Showing results for \"" + response.successfulQuery + "\".");
            } else {
                statusLabel.setText("✅ Found " + originalResults.size() + " books for \"" + query + "\".");
            }

            populateCategoryDropdown(this.originalResults);
            sortCombo.getSelectionModel().select("Relevance");
            categoryCombo.getSelectionModel().select("All Categories");
            displayBooks(this.currentDisplayList);
        }
    }

    private void populateCategoryDropdown(List<Book> books) {
        List<String> categories = books.stream()
                .map(Book::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        categories.add(0, "All Categories"); // Add "All" option at top
        categoryCombo.setItems(FXCollections.observableArrayList(categories));
    }

    private void applySortAndFilter() {
        if (originalResults.isEmpty()) return;

        List<Book> temp = new ArrayList<>(originalResults);

        String selectedCat = categoryCombo.getValue();
        if (selectedCat != null && !selectedCat.equals("All Categories")) {
            temp = temp.stream()
                    .filter(b -> selectedCat.equals(b.getCategory()))
                    .collect(Collectors.toList());
        }

        String sortType = sortCombo.getValue();
        if (sortType != null) {
            switch (sortType) {
                case "Rating: High to Low":
                    temp.sort(Comparator.comparing(Book::getRating).reversed());
                    break;
                case "Title: A-Z":
                    temp.sort(Comparator.comparing(Book::getTitle));
                    break;
                case "Relevance":
                default:
                    // For relevance, we rely on the order in 'originalResults'.
                    // We filter 'originalResults' again to preserve that order.
                    // (The stream filter above already preserved relative order)
                    break;
            }
        }

        this.currentDisplayList = temp;
        displayBooks(this.currentDisplayList);
    }

    @FXML
    private void handleClearFilter() {
        sortCombo.getSelectionModel().select("Relevance");
        categoryCombo.getSelectionModel().select("All Categories");
        applySortAndFilter();
    }


    private void displayBooks(List<Book> books) {
        resultsContainer.getChildren().clear();

        int limit = Math.min(books.size(), 12);

        if (limit == 0) {
            return;
        }

        for (int i = 0; i < limit; i++) {
            Book book = books.get(i);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/gui/fxml/BookCard.fxml"));
                Node card = loader.load();

                BookCardController cardController = loader.getController();
                cardController.setData(book);

                card.setOnMouseClicked(e -> {
                    service.logClick(searchField.getText().isEmpty() ? "trending_click" : searchField.getText(), book.getBookId());
                    openDetailsView(book);
                });

                resultsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openDetailsView(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/gui/fxml/BookDetailView.fxml"));
            Parent detailRoot = loader.load();

            BookDetailController controller = loader.getController();

            Stage currentStage = (Stage) searchField.getScene().getWindow();
            Scene currentScene = searchField.getScene();

            controller.setBookData(book, service, currentStage, currentScene);

            currentStage.setScene(new Scene(detailRoot));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleRequest() {
        // Replace with your actual Google Form Link
        String formUrl = "https://forms.gle/JE9EYgq4bXsJincM7";

        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(formUrl));
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback for some linux distros or weird setups
            System.out.println("Could not open browser. Link: " + formUrl);
        }
    }

}