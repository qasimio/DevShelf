package features.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Book;
import domain.SearchResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReRanker {

    private final Map<Integer, Book> bookMap;
    private final Map<Integer, Double> popularityMap;

    // --- Weights ---
    private static final double W_TFIDF = 0.7;
    private static final double W_POPULARITY = 0.20;
    private static final double W_RATING = 0.10;

    // --- NEW: A massive boost for exact title matches ---
    private static final double EXACT_TITLE_BOOST = 10.0;

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
     * Re-ranks a list using the Master Formula AND applies boosts.
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

            // --- THE MASTER FORMULA ---
            double finalScore = (W_TFIDF * tfIdfScore) +
                    (W_RATING * normalizedRating) +
                    (W_POPULARITY * popularityScore);

            // --- NEW: Apply Exact Match Boost ---
            // If the query is an exact title match, it gets a massive boost.
            if (book.getTitle() != null && book.getTitle().toLowerCase().equals(cleanQuery)) {
                finalScore += EXACT_TITLE_BOOST;
            }

            reRankedResults.add(new SearchResult(docId, finalScore));
        }

        // Sort by the new, final score
        Collections.sort(reRankedResults);

        // The check for "lower index" (Book ID) is now handled.
        // If two books have the *exact* same title match and score,
        // we can add a tie-breaker.

        // Tie-breaker logic (optional but good):
        // If two scores are identical, sort by the lower Book ID.
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

    public Map<Integer, Double> getPopularityMap() {
        return this.popularityMap;
    }
}