package domain;

import lombok.Getter;

import java.util.List;
import java.util.Map;

public class SearchIndexData {
    @Getter
    private Map<String, List<Posting>> invertedIndex;
    @Getter
    private Map<Integer, Map<String, Double>> tfIdfVectors;
    @Getter
    private Map<String, Double> idfScores;

    public SearchIndexData() {}

    public SearchIndexData(Map<String, List<Posting>> invertedIndex, Map<Integer, Map<String, Double>> tfIdfVectors, Map<String, Double> idfScores) {
        this.invertedIndex = invertedIndex;
        this.tfIdfVectors = tfIdfVectors;
        this.idfScores = idfScores;
    }


}

/**
* Contain Our InvertedIndex and Tf-Idf ready to be loaded into json
 * json to Map and Map to json
 */