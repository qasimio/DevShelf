package core;

import domain.Book;
import domain.SearchResult;
import features.recommendation.Graph;
import features.search.QueryProcessor;
import features.search.ReRanker;
import features.search.Suggester;
import utils.LoggingService;
import ui.cli.CliView;
import utils.BookFilter;
import utils.BookSorter;

import java.util.*;
import java.util.stream.Collectors;

public class BookSearchEngine {

    // --- Services (Injected) ---
    private final Map<Integer, Book> bookMap;
    private final QueryProcessor queryProcessor;
    private final ReRanker reRanker;
    private final Suggester suggester;
    private final Graph graph;
    private final LoggingService loggingService;
    private final CliView view;

    // --- State for the action loop ---
    private Map<String, Object> currentFilters;
    private String currentSortMode;
    private boolean isSortAscending;

    public BookSearchEngine(Map<Integer, Book> bookMap, QueryProcessor queryProcessor,
                            ReRanker reRanker, Suggester suggester, Graph graph,
                            LoggingService loggingService, CliView view) {
        this.bookMap = bookMap;
        this.queryProcessor = queryProcessor;
        this.reRanker = reRanker;
        this.suggester = suggester;
        this.graph = graph;
        this.loggingService = loggingService;
        this.view = view;

        // Initialize state
        this.currentFilters = new HashMap<>();
        this.currentSortMode = "relevance";
        this.isSortAscending = false;
    }

    public void run() {
        view.showWelcomeMessage(bookMap.size());
        while (true) {
            String query = view.getSearchQuery();
            if (query.equalsIgnoreCase("exit")) {
                view.showExitMessage();
                break;
            }
            if (query.isEmpty()) continue;

            processQuery(query);
        }
    }

    private void processQuery(String query) {
        // --- 1. GET & RERANK ---
        List<SearchResult> tfIdfResults = queryProcessor.search(query);
        if (tfIdfResults.isEmpty()) {
            handleNoResults(query);
            return;
        }

        // Pass query to reRanker for exact-match boosting
        List<SearchResult> rankedResults = reRanker.reRank(tfIdfResults, query);

        // This is our master list. We *never* modify it.
        final List<Book> initialBooks = rankedResults.stream()
                .map(r -> bookMap.get(r.getDocId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // --- 2. RESET STATE for the new query ---
        clearFiltersAndSort();

        // --- 3. THE FLAT ACTION LOOP ---
        while (true) {
            // --- A. APPLY STATE ---
            // Create a new list *every time* from the original results
            List<Book> filteredBooks = applyFilters(initialBooks);
            applySort(filteredBooks); // Sort the new list

            // --- B. DISPLAY ---
            view.showResults(query, filteredBooks);

            // --- C. GET ACTION ---
            String choice = view.getActionPrompt();

            switch (choice) {
                case "f": handleFilterMenu(); break;
                case "s": handleSortMenu(); break;
                case "r": handleRelated(filteredBooks); break;
                case "l": logUserClick(filteredBooks, query); break;
                case "c": clearFiltersAndSort(); view.showMessage("Filters and sort reset."); break;
                case "n": return; // Exits this method, goes back to main 'while' loop
                case "e": view.showExitMessage(); System.exit(0);
                default: view.showMessage("Invalid command. Try again.");
            }
        }
    }

    private List<Book> applyFilters(List<Book> originalBooks) {
        List<Book> filteredList = new ArrayList<>(originalBooks);
        if (currentFilters.containsKey("author")) {
            filteredList = BookFilter.filterByAuthor(filteredList, (String) currentFilters.get("author"));
        }
        if (currentFilters.containsKey("category")) {
            filteredList = BookFilter.filterByCategory(filteredList, (String) currentFilters.get("category"));
        }
        if (currentFilters.containsKey("language")) {
            filteredList = BookFilter.filterByLanguage(filteredList, (String) currentFilters.get("language"));
        }
        if (currentFilters.containsKey("rating")) {
            filteredList = BookFilter.filterByRating(filteredList, (Double) currentFilters.get("rating"));
        }
        return filteredList;
    }

    private void applySort(List<Book> books) {
        if (currentSortMode.equals("rating")) {
            BookSorter.sortByRating(books, isSortAscending);
        } else if (currentSortMode.equals("title")) {
            BookSorter.sortByTitle(books, isSortAscending);
        }
        // If "relevance", we do nothing. The list is already in relevance order.
    }

    private void handleFilterMenu() {
        int choice = view.getFilterChoice();
        switch (choice) {
            case 1: currentFilters.put("author", view.getFilterValue("Author")); break;
            case 2: currentFilters.put("category", view.getFilterValue("Category")); break;
            case 3: currentFilters.put("language", view.getFilterValue("Language")); break;
            case 4: currentFilters.put("rating", Double.parseDouble(view.getFilterValue("Min Rating"))); break;
            default: view.showMessage("Invalid filter choice.");
        }
    }

    private void handleSortMenu() {
        int choice = view.getSortChoice();
        this.isSortAscending = view.getSortAscending();

        switch (choice) {
            case 2: this.currentSortMode = "rating"; break;
            case 3: this.currentSortMode = "title"; break;
            default: this.currentSortMode = "relevance";
        }
    }

    private void clearFiltersAndSort() {
        this.currentFilters.clear();
        this.currentSortMode = "relevance";
        this.isSortAscending = false;
    }

    private void handleRelated(List<Book> books) {
        if (books.isEmpty()) {
            view.showMessage("No results to base recommendations on.");
            return;
        }
        List<String> related = graph.recommendPopularBooks(books.get(0).getTitle(), 5,
                reRanker.getPopularityMap(), new ArrayList<>(bookMap.values()));
        view.showRelated(related);
    }

    private void handleNoResults(String query) {
        String suggestion = suggester.suggestSimilar(query);
        view.showSuggestion(suggestion);
        // ... (rest of suggestion logic) ...
    }

    private void logUserClick(List<Book> booksToDisplay, String query) {
        if (booksToDisplay.isEmpty()) return;

        int choice = view.getClickChoice(); // e.g., user types '1'
        if (choice > 0 && choice <= 7 && choice <= booksToDisplay.size()) {
            int clickedId = booksToDisplay.get(choice - 1).getBookId();
            loggingService.logClick(query, clickedId);
            view.showMessage("✅ Logged click for book ID: " + clickedId);
        } else {
            view.showMessage("⚠️ Invalid number, no click logged.");
        }
    }
}