package features.search;

import java.util.*;

public class Suggester {
    private final List<String> allTitles;
    private final Set<String> stopwords;

    public Suggester(List<String> titles, Set<String> stopwords) {
        this.allTitles = titles != null ? titles : new ArrayList<>();
        this.stopwords = stopwords != null ? stopwords : new HashSet<>();
    }


    public String suggestSimilar(String query) {
        if (query == null || query.trim().isEmpty()) return null;

        String cleanedQuery = preprocess(query);
        if (cleanedQuery.isEmpty()) return null;

        double bestScore = 0.0;
        String bestMatch = null;

        for (String title : allTitles) {
            if (title == null || title.trim().isEmpty()) continue;

            String cleanedTitle = preprocess(title);
            if (cleanedTitle.isEmpty()) continue;

            double globalSim = calculateSimilarity(cleanedQuery, cleanedTitle);
            double wordSim = wordLevelSimilarity(cleanedQuery, cleanedTitle);
            double finalScore = 0.65 * wordSim + 0.35 * globalSim;

            if (finalScore > bestScore) {
                bestScore = finalScore;
                bestMatch = title;
            }
        }

        // Only suggest if score >= threshold
        return (bestMatch != null && bestScore >= 0.6) ? bestMatch : null;
    }

    // --- Preprocess text: lowercase, remove punctuation, remove stopwords ---
    private String preprocess(String text) {
        if (text == null) return "";
        text = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").trim();
        StringBuilder sb = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (word.length() > 1 && !stopwords.contains(word)) {
                sb.append(word).append(" ");
            }
        }
        return sb.toString().trim();
    }

    // --- Global similarity using Levenshtein ---
    private double calculateSimilarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) levenshteinDistance(s1, s2) / maxLen);
    }

    // --- Word-level similarity ---
    private double wordLevelSimilarity(String query, String title) {
        if (query.isEmpty()) return 0.0;
        String[] qWords = query.split(" ");
        String[] tWords = title.split(" ");
        if (tWords.length == 0) return 0.0;

        double total = 0.0;
        for (String qw : qWords) {
            double best = 0.0;
            for (String tw : tWords) {
                double sim = calculateSimilarity(qw, tw);
                if (sim > best) best = sim;
                if (best >= 0.95) break;
            }
            total += best;
        }
        return total / qWords.length;
    }

    // --- Levenshtein distance ---
    public int levenshteinDistance(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[m][n];
    }
}
