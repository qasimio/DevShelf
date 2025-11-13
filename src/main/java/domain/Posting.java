package domain;
// Helper - Posting class - made public can be accessed from anywhere!

import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Posting {
    private int docId;
    private int freq;
    private List<Integer> positions;

    public Posting() {}

    public Posting(int docId, int freq, List<Integer> positions) {
        this.docId = docId;
        this.freq = freq;
        this.positions = positions;
    }

    @Override
    public String toString() {
        return "{docId = " + docId + ", freq = " + freq + ", Pos: " + positions + "}";
    }

}
