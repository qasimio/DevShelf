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

    // ✅ NEW: Dropdowns
    @FXML private ComboBox<String> sortCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ContextMenu suggestionsPopup = new ContextMenu();

    @Setter
    private DevShelfService service;

    // We keep two lists:
    // 1. originalResults: The exact order returned by the "Brain" (Relevance)
    // 2. currentDisplayList: The list currently shown (filtered/sorted)
    private List<Book> originalResults = new ArrayList<>();
    private List<Book> currentDisplayList = new ArrayList<>();

    @FXML
    public void initialize() {
        // 1. Setup Sort/Filter Options (Existing code)
        sortCombo.setItems(FXCollections.observableArrayList(
                "Relevance", "Rating: High to Low", "Title: A-Z"
        ));
        sortCombo.getSelectionModel().selectFirst();
        sortCombo.setOnAction(e -> applySortAndFilter());
        categoryCombo.setOnAction(e -> applySortAndFilter());

        // 2. Load Trending Books (Existing code)
        javafx.application.Platform.runLater(() -> {
            if (service != null) loadTrending();
        });

        // --- 3. NEW: Autocomplete Logic ---
        setupAutocomplete();
    }

    private void setupAutocomplete() {
        // Hide the popup if the user clicks away
        suggestionsPopup.setAutoHide(true);

        // Listen to every character typed
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Don't search if empty or very short (optional, here we do > 1 char)
            if (newValue == null || newValue.trim().length() < 1) {
                suggestionsPopup.hide();
                return;
            }

            // 1. Get Predictions from Service
            List<String> suggestions = service.getAutoCompletions(newValue);

            // 2. If no matches, hide popup
            if (suggestions.isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            // 3. Populate the Popup Menu
            suggestionsPopup.getItems().clear();
            for (String suggestion : suggestions) {
                MenuItem item = new MenuItem(suggestion);

                // Add "Click" logic to the suggestion
                item.setOnAction(e -> {
                    // Fill the search bar with the clicked suggestion
                    searchField.setText(suggestion);
                    // Hide the popup
                    suggestionsPopup.hide();
                    // Trigger the search immediately
                    handleSearch();
                });

                suggestionsPopup.getItems().add(item);
            }

            // 4. Show the popup below the search field
            if (!suggestionsPopup.isShowing()) {
                suggestionsPopup.show(searchField, Side.BOTTOM, 0, 0);
            }
        });

        // Hide popup if the user presses ENTER (because they are submitting)
        searchField.setOnAction(e -> suggestionsPopup.hide());
    }

    @FXML
    private void handleHome() {
        searchField.clear();
        loadTrending();
        // Reset dropdowns
        sortCombo.getSelectionModel().selectFirst();
        categoryCombo.getSelectionModel().select("All Categories");
    }

    private void loadTrending() {
        statusLabel.setText("🔥 Trending Books - Top Picks by Users");
        List<Book> trending = service.getTrendingBooks();

        // We treat these as our "Current Results" so filters work on them too!
        this.originalResults = trending;
        this.currentDisplayList = trending;

        populateCategoryDropdown(trending);
        displayBooks(trending);
    }


    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) return;

        statusLabel.setText("Searching...");

        // 1. Get results from Backend
        SearchResponse response = service.search(query);

        // 2. Save original copy (for "Relevance" sort)
        this.originalResults = response.books;
        this.currentDisplayList = new ArrayList<>(this.originalResults);

        if (this.originalResults.isEmpty()) {
            statusLabel.setText("❌ No results found.");
            resultsContainer.getChildren().clear();
            categoryCombo.setItems(FXCollections.observableArrayList()); // Clear dropdown
        } else {
            String msg = response.isSuggestion ?
                    "💡 Showing results for: " + response.successfulQuery :
                    "✅ Found " + originalResults.size() + " books.";
            statusLabel.setText(msg);

            // 3. Setup Filters dynamically based on results
            populateCategoryDropdown(this.originalResults);

            // 4. Reset dropdowns to default
            sortCombo.getSelectionModel().select("Relevance");
            categoryCombo.getSelectionModel().select("All Categories");

            // 5. Display
            displayBooks(this.currentDisplayList);
        }
    }

    // ✅ Extracts unique categories from the search results
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

    // ✅ The Master Logic for Sorting & Filtering
    private void applySortAndFilter() {
        if (originalResults.isEmpty()) return;

        // 1. Start with the full list
        List<Book> temp = new ArrayList<>(originalResults);

        // 2. Apply Category Filter
        String selectedCat = categoryCombo.getValue();
        if (selectedCat != null && !selectedCat.equals("All Categories")) {
            temp = temp.stream()
                    .filter(b -> selectedCat.equals(b.getCategory()))
                    .collect(Collectors.toList());
        }

        // 3. Apply Sorting
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

        // 4. Update Display
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

        // 🛑 HARD LIMIT: Only show top 12.
        // Even if 'books' has 200 items sorted by rating, we only show the top 50.
        int limit = Math.min(books.size(), 12);

        if (limit == 0) {
            // Optional: Show a "No Books" label if empty
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

            controller.setBookData(book, currentStage, currentScene);
            currentStage.setScene(new Scene(detailRoot));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}