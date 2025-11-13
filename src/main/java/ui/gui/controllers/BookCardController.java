package ui.gui.controllers;

import domain.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BookCardController {

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label ratingLabel;
    @FXML private Label categoryLabel;

    public void setData(Book book) {
        titleLabel.setText(book.getTitle());
        authorLabel.setText(book.getAuthor() != null ? book.getAuthor() : "Unknown Author");
        ratingLabel.setText(String.format("%.1f ★", book.getRating()));
        categoryLabel.setText(book.getCategory() != null ? book.getCategory() : "General");
    }
}