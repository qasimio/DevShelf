package features.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Book;
import domain.SearchResult;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReRanker {

    private final Map<Integer, Book> bookMap;
    @Getter
    private final Map<Integer, Double> popularityMap;

    // --- Weights ---
    private static final double W_TFIDF = 0.7;      // 70%
    private static final double W_POPULARITY = 0.20; // 20%
    private static final double W_RATING = 0.10;     // 10%

    // --- NEW: Tiered Title Boosting ---
    // These are applied *on top* of the final score.
    private static final double EXACT_TITLE_BOOST = 10.0; // For "python" matching "Python"
    private static final double STARTS_WITH_BOOST = 5.0;  // For "hands-on" matching "Hands-On Machine Learning..."
    private static final double CONTAINS_BOOST = 2.0;     // For "c++" matching "Effective C++"

    public ReRanker(Map<Integer, Book> bookMap, String popularityFilePath) {
        this.bookMap = bookMap;
        this.popularityMap = loadPopularity(popularityFilePath);
    }

    private Map<Integer, Double> loadPopularity(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("Popularity file not found. Skipping popularity boost.");
                return new HashMap<>();
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            System.err.println("Error loading popularity file: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Returns the DocIDs of the most clicked books, sorted by popularity.
     */
    public List<Integer> getTopTrending(int limit) {
        return popularityMap.entrySet().stream()
                // Sort by Value (Count) Descending
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                // Take the top N
                .limit(limit)
                // Extract the Key (BookID)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    /**
     * Re-ranks a list using the Master Formula AND applies tiered boosts.
     * @param tfIdfResults The raw results from the QueryProcessor.
     * @param query The original user search query.
     */
    public List<SearchResult> reRank(List<SearchResult> tfIdfResults, String query) {
        List<SearchResult> reRankedResults = new ArrayList<>();
        String cleanQuery = query.trim().toLowerCase();

        for (SearchResult oldResult : tfIdfResults) {
            int docId = oldResult.getDocId();
            Book book = bookMap.get(docId);
            if (book == null) continue;

            double tfIdfScore = oldResult.getScore();
            double normalizedRating = book.getRating() / 5.0;
            double popularityScore = popularityMap.getOrDefault(docId, 0.0);

            // --- 1. THE MASTER FORMULA (Base Score) ---
            double finalScore = (W_TFIDF * tfIdfScore) +
                    (W_RATING * normalizedRating) +
                    (W_POPULARITY * popularityScore);

            // --- 2. APPLY TIERED TITLE BOOSTS ---
            // We apply boosts *after* calculating the base score.
            if (book.getTitle() != null) {
                String title = book.getTitle().toLowerCase();

                // Use 'if-else if' to prevent stacking boosts
                if (title.equals(cleanQuery)) {
                    finalScore += EXACT_TITLE_BOOST;
                } else if (title.startsWith(cleanQuery)) {
                    finalScore += STARTS_WITH_BOOST;
                } else if (title.contains(cleanQuery)) {
                    finalScore += CONTAINS_BOOST;
                }
            }

            reRankedResults.add(new SearchResult(docId, finalScore));
        }

        // --- 3. SORT BY FINAL SCORE & TIE-BREAK ---
        // Your tie-breaker logic is perfect and is preserved here.
        reRankedResults.sort((r1, r2) -> {
            int scoreCompare = Double.compare(r2.getScore(), r1.getScore());
            if (scoreCompare != 0) {
                return scoreCompare; // Scores are different, just use that
            }
            // Scores are identical, use lower Book ID as tie-breaker
            return Integer.compare(bookMap.get(r1.getDocId()).getBookId(), bookMap.get(r2.getDocId()).getBookId());
        });

        return reRankedResults;
    }
}