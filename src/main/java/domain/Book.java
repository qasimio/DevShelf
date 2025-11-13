package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Book {
    private int bookId;
    private String title;
    private String author;
    private String description;
    private String progLang;
    private String category;
    private String[] tag;
    private float rating;
    private String coverUrl;
    private String downLink;

    public Book() {}

    public Book(int bookId, String title, String author, String description, String progLang,
                String category, String[] tag, float rating, String coverUrl, String downLink) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.progLang = progLang;
        this.category = category;
        this.tag = tag;
        this.rating = rating;
        this.coverUrl = coverUrl;
        this.downLink = downLink;
    }

}

