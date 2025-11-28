package ui.gui.services;

import domain.Book;
import domain.SearchResult;
import features.recommendation.Graph;
import features.search.PhraseCompletion;
import features.search.QueryProcessor;
import features.search.ReRanker;
import features.search.Suggester;
import features.search.PhraseCompletion;
import utils.LoggingService; // (Moved to utils? Check your imports)

import java.util.*;
import java.util.stream.Collectors;

public class DevShelfService {

    private final Map<Integer, Book> bookMap;
    private final QueryProcessor queryProcessor;
    private final ReRanker reRanker;
    private final Suggester suggester;
    private final LoggingService loggingService;
    private final Graph graph;
private final PhraseCompletion phraseCompletion;
    public DevShelfService(Map<Integer, Book> bookMap, QueryProcessor queryProcessor,
                           ReRanker reRanker, Suggester suggester, Graph graph, LoggingService loggingService) {
        this.bookMap = bookMap;
        this.queryProcessor = queryProcessor;
        this.reRanker = reRanker;
        this.suggester = suggester;
        this.graph = graph;
        this.loggingService = loggingService;
        phraseCompletion=new PhraseCompletion();
        indexAllBooksForAutocomplete();
    }

    public SearchResponse search(String query) {
        System.out.println("🔍 GUI Processing Query: [" + query + "]");

        // 1. Raw Search
        List<SearchResult> results = queryProcessor.search(query);
        String usedQuery = query;
        boolean isSuggestion = false;

        // 2. Handle No Results / Typos
        if (results.isEmpty()) {
            String suggestion = suggester.suggestSimilar(query);
            if (suggestion != null) {
                System.out.println("💡 Suggestion found: " + suggestion);
                results = queryProcessor.search(suggestion);
                usedQuery = suggestion;
                isSuggestion = true;
            }
        }

        // 3. Re-Rank (The "Intelligence" Layer)
        // We explicitly re-rank to ensure popularity is accounted for
        List<SearchResult> rankedResults = reRanker.reRank(results, usedQuery);

        // --- DEBUGGING: Print top 5 results to Console to compare with CLI ---
        System.out.println("📊 Top 5 Results (DocID : Score):");
        for (int i = 0; i < Math.min(5, rankedResults.size()); i++) {
            SearchResult r = rankedResults.get(i);
            System.out.printf("   [%d] DocID: %d | Score: %.4f%n", i+1, r.getDocId(), r.getScore());
        }
        // ---------------------------------------------------------------------

        // 4. Convert to Books (Preserving Order strictly)
        List<Book> books = new ArrayList<>();
        for (SearchResult res : rankedResults) {
            Book b = bookMap.get(res.getDocId());
            if (b != null) {
                books.add(b);
            }
        }

        return new SearchResponse(books, isSuggestion, usedQuery);
    }

    /**
     * Returns a list of up to 5 titles that start with the given prefix.
     * Used for Autocomplete.
     */
    // In DevShelfService.java:
    private void indexAllBooksForAutocomplete() {
        // ...
        for (Book book : bookMap.values()) {
            String fullTitle = book.getTitle();
            // ... (check for null)

            // ✅ MODIFIED SPLIT: Split only by spaces or commas, preserving symbols/punctuation.
            // We split by one or more spaces or commas. Adjust based on common separators in your titles.
            String[] words = fullTitle.split("[\\s,]+");

            for (String word : words) {
                // Trim to handle leading/trailing spaces from complex splitting
                String cleanWord = word.trim();
                if (!cleanWord.isEmpty()) {
                    // Insert the word (e.g., "C++" or "C#")
                    phraseCompletion.insertWordAndTitle(cleanWord, fullTitle);
                }
            }
        }
        // ...
    }
    // In DevShelfService.java:

    public List<String> getAutoCompletions(String prefix) {
        if (prefix == null || prefix.isEmpty()) return Collections.emptyList();

        // 1. Get all matches from the Trié (fast!)
        List<String> allMatches = phraseCompletion.Complete(prefix, 50); // Get a larger sample

        String lowerPrefix = prefix.toLowerCase();

        // 2. Separate into two groups
        List<String> startingMatches = new ArrayList<>();
        List<String> midTitleMatches = new ArrayList<>();

        for (String title : allMatches) {
            // Check if the title ITSELF starts with the prefix (e.g., prefix "Des" matches title "Design Patterns")
            if (title.toLowerCase().startsWith(lowerPrefix)) {
                startingMatches.add(title);
            } else {
                // This is a mid-title word match (e.g., prefix "Pat" matches word "Patterns" in "Design Patterns")
                midTitleMatches.add(title);
            }
        }

        // 3. Combine and limit
        // Start with strict prefix matches, then append mid-title matches
        List<String> finalSuggestions = new ArrayList<>();
        finalSuggestions.addAll(startingMatches);

        // Add mid-title matches until the limit (5) is hit
        for (String title : midTitleMatches) {
            if (!finalSuggestions.contains(title)) { // Avoid duplicates if a title was added by 'startingMatches'
                finalSuggestions.add(title);
            }
        }

        // 4. Return the top 5
        return finalSuggestions.stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<Book> getRecommendationsFor(Book book) {
        if (book == null) return Collections.emptyList();

        // 1. Get the list of related titles, sorted by popularity
        List<String> relatedTitles = graph.recommendPopularBooks(
                book.getTitle(),
                5, // Get top 5
                reRanker.getPopularityMap() // Re-use the map from the ReRanker
        );

        // 2. Convert the list of String titles back into Book objects
        return relatedTitles.stream()
                .map(this::findBookByTitle) // Use our helper
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to find a book by its exact title (case-insensitive).
     */
    private Book findBookByTitle(String title) {
        String normalizedTitle = title.toLowerCase().trim();
        // This is faster than streaming the map values every time
        for (Book book : bookMap.values()) {
            if (book.getTitle() != null && book.getTitle().toLowerCase().trim().equals(normalizedTitle)) {
                return book;
            }
        }
        return null;
    }

    public List<Book> getTrendingBooks() {
        // 1. Get Top 10 IDs from ReRanker
        List<Integer> trendingIds = reRanker.getTopTrending(10);

        // 2. Convert IDs to Book Objects
        List<Book> trendingBooks = new ArrayList<>();
        for (Integer id : trendingIds) {
            Book b = bookMap.get(id);
            if (b != null) {
                trendingBooks.add(b);
            }
        }

        // 3. If no popularity data exists yet (clean install),
        // just return the first 10 books from the database as a fallback.
        if (trendingBooks.isEmpty()) {
            return bookMap.values().stream().limit(10).collect(Collectors.toList());
        }

        return trendingBooks;
    }


    public void logClick(String query, int bookId) {
        System.out.println("🖱️ Click Logged: BookID " + bookId + " for query '" + query + "'");
        loggingService.logClick(query, bookId);
    }

    // Helper class remains the same...
    public static class SearchResponse {
        public final List<Book> books;
        public final boolean isSuggestion;
        public final String successfulQuery;

        public SearchResponse(List<Book> books, boolean isSuggestion, String successfulQuery) {
            this.books = books;
            this.isSuggestion = isSuggestion;
            this.successfulQuery = successfulQuery;
        }
    }
}