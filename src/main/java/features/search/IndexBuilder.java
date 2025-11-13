package features.search;

import domain.Book;
import domain.Posting;
import lombok.Getter;
import utils.TextProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexBuilder {

    @Getter
    private final Map<String, List<Posting>> invertedIndex;
    private final TextProcessor textProcessor;

    public IndexBuilder(TextProcessor textProcessor) {
        this.textProcessor = textProcessor;
        this.invertedIndex = new HashMap<>();
    }

    public void indexDocument(Book book) {

        StringBuilder bookData = new StringBuilder();
        bookData.append(book.getTitle()).append(" ");
        bookData.append(book.getAuthor()).append(" ");
        bookData.append(book.getDescription()).append(" ");
        bookData.append(book.getCategory()).append(" ");
        bookData.append(book.getProgLang()).append(" ");
        bookData.append(String.join(" ", book.getTag()));

        List<String> stemmedTokens = textProcessor.process(bookData.toString());
        Map<String, List<Integer>> termPositions = new HashMap<>();
        for (int pos = 0; pos < stemmedTokens.size(); pos++) {
            String term = stemmedTokens.get(pos);
            termPositions.computeIfAbsent(term, k -> new ArrayList<>()).add(pos);
        }
        for (Map.Entry<String, List<Integer>> entry : termPositions.entrySet()) {
            String term = entry.getKey();
            List<Integer> positions = entry.getValue();

            Posting posting = new Posting(book.getBookId(), positions.size(), positions);

            invertedIndex.computeIfAbsent(term, k -> new ArrayList<>()).add(posting);
        }

    }

}