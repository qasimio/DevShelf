package ui.gui.services;

import domain.Book;
import domain.SearchResult;
import features.search.QueryProcessor;
import features.search.ReRanker;
import features.search.Suggester;
import utils.LoggingService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DevShelfService {

    private final Map<Integer, Book> bookMap;
    private final QueryProcessor queryProcessor;
    private final ReRanker reRanker;
    private final Suggester suggester;
    private final LoggingService loggingService;

    public DevShelfService(Map<Integer, Book> bookMap, QueryProcessor queryProcessor,
                           ReRanker reRanker, Suggester suggester, LoggingService loggingService) {
        this.bookMap = bookMap;
        this.queryProcessor = queryProcessor;
        this.reRanker = reRanker;
        this.suggester = suggester;
        this.loggingService = loggingService;
    }

    /**
     * Searches for books. If no results, tries to correct the spelling.
     * Returns a wrapper object or list.
     */
    public SearchResponse search(String query) {
        // 1. Raw Search
        List<SearchResult> results = queryProcessor.search(query);
        String usedQuery = query;
        boolean isSuggestion = false;

        // 2. Handle No Results / Typos
        if (results.isEmpty()) {
            String suggestion = suggester.suggestSimilar(query);
            if (suggestion != null) {
                results = queryProcessor.search(suggestion);
                usedQuery = suggestion;
                isSuggestion = true;
            }
        }

        // 3. Re-Rank
        List<SearchResult> rankedResults = reRanker.reRank(results, usedQuery);

        // 4. Convert to Books
        List<Book> books = rankedResults.stream()
                .map(r -> bookMap.get(r.getDocId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new SearchResponse(books, isSuggestion, usedQuery);
    }

    public void logClick(String query, int bookId) {
        loggingService.logClick(query, bookId);
    }

    // --- Helper Class to pass data back to GUI ---
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