package utils;

import domain.Book;
import java.util.List;
import java.util.stream.Collectors;

public class BookFilter {

    public static List<Book> filterByCategory(List<Book> books, String category) {
        if (category == null || category.trim().isEmpty()) {
            return books;
        }
        final String searchCategory = category.trim().toLowerCase();
        return books.stream()
                .filter(b -> b.getCategory() != null && b.getCategory().toLowerCase().contains(searchCategory))
                .collect(Collectors.toList());
    }

    public static List<Book> filterByAuthor(List<Book> books, String author) {
        if (author == null || author.trim().isEmpty()) {
            return books;
        }
        final String searchAuthor = author.trim().toLowerCase();
        return books.stream()
                .filter(b -> b.getAuthor() != null && b.getAuthor().toLowerCase().contains(searchAuthor))
                .collect(Collectors.toList());
    }

    public static List<Book> filterByLanguage(List<Book> books, String language) {
        if (language == null || language.trim().isEmpty()) {
            return books;
        }
        final String searchLanguage = language.trim().toLowerCase();
        return books.stream()
                .filter(b -> b.getProgLang() != null && b.getProgLang().toLowerCase().contains(searchLanguage))
                .collect(Collectors.toList());
    }

    public static List<Book> filterByRating(List<Book> books, double minRating) {
        if (minRating <= 0.0) {
            return books;
        }
        return books.stream()
                .filter(b -> b.getRating() >= minRating)
                .collect(Collectors.toList());
    }
}
