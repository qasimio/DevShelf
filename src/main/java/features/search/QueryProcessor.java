package features.search;

import domain.Posting;
import domain.SearchResult;
import utils.TextProcessor;
import java.util.*;

public class QueryProcessor {
    private final TextProcessor textProcessor;
    private final Map<String, List<Posting>> invertedIndex;
    private final Map<Integer, Map<String, Double>> tfIdfVectors;
    private final Map<String, Double> idfScores;

    public QueryProcessor(TextProcessor textProcessor,
                          Map<String, List<Posting>> invertedIndex,
                          Map<Integer, Map<String, Double>> tfIdfVectors,
                          Map<String, Double> idfScores) {
        this.textProcessor = textProcessor;
        this.invertedIndex = invertedIndex;
        this.tfIdfVectors = tfIdfVectors;
        this.idfScores = idfScores;
    }

    public List<SearchResult> search(String rawQuery) {
        // 1. Process the query (same as indexing)
        List<String> queryTerms = textProcessor.process(rawQuery);

        // 2. Calculate the query's own TF-IDF vector
        Map<String, Double> queryVector = calculateQueryVector(queryTerms);
        if (queryVector.isEmpty()) {
            return Collections.emptyList(); // No valid terms
        }

        // 3. Find all documents that match *any* query term
        Set<Integer> matchingDocIds = findMatchingDocuments(queryTerms);

        // 4. Score each matching document
        List<SearchResult> results = new ArrayList<>();
        for (int docId : matchingDocIds) {
            Map<String, Double> docVector = tfIdfVectors.get(docId);

            // This is the core ranking logic!
            double score = cosineSimilarity(queryVector, docVector);

            if (score > 0) {
                results.add(new SearchResult(docId, score));
            }
        }

        // 5. Rank (sort) the results by score
        Collections.sort(results);

        return results;
    }

    private Set<Integer> findMatchingDocuments(List<String> queryTerms) {
        Set<Integer> docIds = new HashSet<>();
        for(String term : queryTerms) {
            List<Posting> postings = invertedIndex.get(term);
            if(postings != null){
                for(Posting p : postings) {
                    docIds.add(p.getDocId());
                }
            }
        }
        return docIds;
    }

    private Map<String, Double> calculateQueryVector(List<String> queryTerms) {
        Map<String, Double> queryVector = new HashMap<>();

        Map<String, Integer> termCounts = new HashMap<>();
        for(String term : queryTerms) {
            termCounts.put(term, termCounts.getOrDefault(term, 0) + 1);
        }

        for(String term : termCounts.keySet()) {
            double tf = 1 + Math.log10(termCounts.get(term));
            double idf = idfScores.getOrDefault(term, 0.0);

            queryVector.put(term, tf * idf);
        }
        return queryVector;
    }

    private double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        if (vec2 == null) {
            return 0.0; // Document has no vector?
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        // Calculate Dot Product and Norm1 (Query)
        for (String term : vec1.keySet()) {
            double vec1Score = vec1.get(term);
            double vec2Score = vec2.getOrDefault(term, 0.0);

            dotProduct += vec1Score * vec2Score;
            norm1 += vec1Score * vec1Score; // norm1 += Math.pow(vec1Score, 2)
        }

        // Calculate Norm2 (Document)
        for (double score : vec2.values()) {
            norm2 += score * score; // norm2 += Math.pow(score, 2)
        }

        // Prevent division by zero
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

}
