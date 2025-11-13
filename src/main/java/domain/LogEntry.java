package domain;

import lombok.Getter;
import java.time.Instant;

@Getter
public class LogEntry {
    private String query;
    private int clickedDocId;
    private String timestamp;

    public LogEntry() {}

    public LogEntry(String query, int clickedDocId) {
        this.query = query;
        this.clickedDocId = clickedDocId;
        this.timestamp = Instant.now().toString();
    }

}
