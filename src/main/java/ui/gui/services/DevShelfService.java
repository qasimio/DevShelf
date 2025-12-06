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

        List<SearchResult> results = queryProcessor.search(query);
        String usedQuery = query;
        boolean isSuggestion = false;

        if (results.isEmpty()) {
            String suggestion = suggester.suggestSimilar(query);
            if (suggestion != null) {
                System.out.println("💡 Suggestion found: " + suggestion);
                results = queryProcessor.search(suggestion);
                usedQuery = suggestion;
                isSuggestion = true;
            }
        }

        List<SearchResult> rankedResults = reRanker.reRank(results, usedQuery);

        System.out.println("📊 Top 5 Results (DocID : Score):");
        for (int i = 0; i < Math.min(5, rankedResults.size()); i++) {
            SearchResult r = rankedResults.get(i);
            System.out.printf("   [%d] DocID: %d | Score: %.4f%n", i+1, r.getDocId(), r.getScore());
        }

        List<Book> books = new ArrayList<>();
        for (SearchResult res : rankedResults) {
            Book b = bookMap.get(res.getDocId());
            if (b != null) {
                books.add(b);
            }
        }

        return new SearchResponse(books, isSuggestion, usedQuery);
    }

    private void indexAllBooksForAutocomplete() {
        for (Book book : bookMap.values()) {
            String fullTitle = book.getTitle();

            String[] words = fullTitle.split("[\\s,]+");

            for (String word : words) {
                String cleanWord = word.trim();
                if (!cleanWord.isEmpty()) {
                    phraseCompletion.insertWordAndTitle(cleanWord, fullTitle);
                }
            }
        }
    }

    public List<String> getAutoCompletions(String prefix) {
        if (prefix == null || prefix.isEmpty()) return Collections.emptyList();

        List<String> allMatches = phraseCompletion.Complete(prefix, 50); // Get a larger sample

        String lowerPrefix = prefix.toLowerCase();

        List<String> startingMatches = new ArrayList<>();
        List<String> midTitleMatches = new ArrayList<>();

        for (String title : allMatches) {
            if (title.toLowerCase().startsWith(lowerPrefix)) {
                startingMatches.add(title);
            } else {
                midTitleMatches.add(title);
            }
        }

        List<String> finalSuggestions = new ArrayList<>();
        finalSuggestions.addAll(startingMatches);

        for (String title : midTitleMatches) {
            if (!finalSuggestions.contains(title)) { // Avoid duplicates if a title was added by 'startingMatches'
                finalSuggestions.add(title);
            }
        }

        return finalSuggestions.stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<Book> getRecommendationsFor(Book book) {
        if (book == null) return Collections.emptyList();

        List<String> relatedTitles = graph.recommendPopularBooks(
                book.getTitle(),
                5, // Get top 5
                reRanker.getPopularityMap() // Re-use the map from the ReRanker
        );

        return relatedTitles.stream()
                .map(this::findBookByTitle) // Use our helper
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Book findBookByTitle(String title) {
        String normalizedTitle = title.toLowerCase().trim();
        for (Book book : bookMap.values()) {
            if (book.getTitle() != null && book.getTitle().toLowerCase().trim().equals(normalizedTitle)) {
                return book;
            }
        }
        return null;
    }

    public List<Book> getTrendingBooks() {
        List<Integer> trendingIds = reRanker.getTopTrending(10);

        List<Book> trendingBooks = new ArrayList<>();
        for (Integer id : trendingIds) {
            Book b = bookMap.get(id);
            if (b != null) {
                trendingBooks.add(b);
            }
        }

        if (trendingBooks.isEmpty()) {
            return bookMap.values().stream().limit(10).collect(Collectors.toList());
        }

        return trendingBooks;
    }


    public void logClick(String query, int bookId) {
        System.out.println("🖱️ Click Logged: BookID " + bookId + " for query '" + query + "'");
        loggingService.logClick(query, bookId);
    }

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