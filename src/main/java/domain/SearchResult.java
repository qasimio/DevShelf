package domain;

import lombok.Getter;

public class SearchResult implements Comparable<SearchResult> {

    @Getter
    private final int docId;
    @Getter
    private final double score;

    public SearchResult(int docId, double score) {
        this.docId = docId;
        this.score = score;
    }

    @Override
    public int compareTo(SearchResult other) {
        return Double.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return "SearchResult {docId = " + docId + ", score = " + score + "}";
    }


}
