package utils;

import domain.Posting;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TfIdfCalculator {

    @Getter
    private Map<Integer, Map<String, Double>> tfIdfVectors = new HashMap<>();
    // This Map store final Tf-Idf of every term that exist in every document

    @Getter
    private Map<String, Double> idfScores = new HashMap<>();
    // store Idf store for every term

    public void calculateIdf(Map<String, List<Posting>> invertedIndex, int totalDocCount) {
        System.out.println("Calculating IDF scores for + " + invertedIndex.size() + " terms...");

        for(String term : invertedIndex.keySet()) {
            int docFrequency = invertedIndex.get(term).size();
            // number of documents in which this term appeared.

            double idf = Math.log10( (double) totalDocCount / docFrequency );

            idfScores.put(term, idf);
            // populating the idfScores Map with term and their idf's

        }
    }


    public void calculateTfIdf(Map<String, List<Posting>> invertedIndex) {

        System.out.println("Calculating TF-IDF vectors for all documents... ");

        for(String term : invertedIndex.keySet()) {

            double idf = idfScores.get(term);
            List<Posting> postings = invertedIndex.get(term);

            for(Posting posting : postings ) {

                int docId = posting.getDocId();
                int termFreq = posting.getFreq();

                double tf = 1 + Math.log10(termFreq);
                double tfIdf = tf * idf;

                Map<String, Double> docVector = tfIdfVectors.computeIfAbsent(docId, k -> new HashMap<>());

                docVector.put(term,tfIdf);
            }
        }
        System.out.println("TF-IDF calculation complete. ");

    }

}
