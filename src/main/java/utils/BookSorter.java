package utils;

import domain.Book;
import java.util.List;

public class BookSorter {

    public static void sortByTitle(List<Book> books, boolean ascending) {
        if (books == null || books.isEmpty()) return;

        books.sort((b1, b2) -> {
            String t1 = b1.getTitle() == null ? "" : b1.getTitle();
            String t2 = b2.getTitle() == null ? "" : b2.getTitle();
            int cmp = t1.compareToIgnoreCase(t2);
            return ascending ? cmp : -cmp;
        });
    }

    public static void sortByRating(List<Book> books, boolean ascending) {
        if (books == null || books.isEmpty()) return;

        books.sort((b1, b2) -> {
            int cmp = Double.compare(b1.getRating(), b2.getRating());
            return ascending ? cmp : -cmp;
        });
    }

    // We've moved the printBooks logic to CliView, where it belongs.
}