package ui.cli;

import domain.Book;
import java.util.List;
import java.util.Scanner;

public class CliView {

    private final Scanner scanner;
    private static final int MAX_DISPLAY_RESULTS = 7; // Top-7 logic lives here

    public CliView() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcomeMessage(int bookCount) {
        System.out.println("\n📘 Welcome to DevShelf! " + bookCount + " books loaded.");
    }

    /**
     * Displays the Top 7 results.
     */
    public void showResults(String query, List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("\n❌ No results found for \"" + query + "\"");
            return;
        }

        System.out.println("\n✅ Showing Top " + Math.min(books.size(), MAX_DISPLAY_RESULTS) + " results for \"" + query + "\":");

        for (int i = 0; i < books.size() && i < MAX_DISPLAY_RESULTS; i++) {
            Book book = books.get(i);
            System.out.printf("  [%d] %s by %s (Rating: %.1f, Lang: %s, Cat: %s)\n",
                    i + 1, book.getTitle(), book.getAuthor(),
                    book.getRating(), book.getProgLang(), book.getCategory());
        }
    }

    /**
     * A single, flat action prompt. This is the core of the new UI.
     */
    public String getActionPrompt() {
        System.out.println("\n--- Actions ---");
        System.out.println("[F]ilter  [S]ort  [R]elated  [L]og Click  [C]lear Filters  [N]ew Search  [E]xit");
        System.out.print("Enter command: ");
        return scanner.nextLine().trim().toLowerCase();
    }

    public String getSearchQuery() {
        System.out.print("\n🔍 Enter search query (or 'exit'): ");
        return scanner.nextLine().trim();
    }

    // --- Methods for getting filter/sort values ---

    public int getSortChoice() {
        System.out.println("Sort by: [1] Relevance (default) [2] Rating [3] Title");
        System.out.print("Choose: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return 1;
        }
    }

    public boolean getSortAscending() {
        System.out.print("Direction: [1] Descending (default) [2] Ascending: ");
        return scanner.nextLine().trim().equals("2");
    }

    public int getFilterChoice() {
        System.out.println("Filter by: [1] Author [2] Category [3] Language [4] Min Rating");
        System.out.print("Choose: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public String getFilterValue(String prompt) {
        System.out.print("Enter value for " + prompt + ": ");
        return scanner.nextLine().trim();
    }

    public int getClickChoice() {
        System.out.print("Enter result number [1-7] to log: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    // --- Simple output methods ---
    public void showMessage(String message) { System.out.println(message); }
    public void showSuggestion(String s) { System.out.println("💡 Did you mean: \"" + s + "\" ?"); }
    public void showRelated(List<String> r) { System.out.println("📚 You might also like: " + r); }
    public void showExitMessage() { System.out.println("\n👋 Thank you for using DevShelf!"); }
}