# Chapter 2: Book (Domain Model)

Welcome back, digital librarian! In our last chapter, [User Interface (UI) Presentation](01_user_interface__ui__presentation_.md), we explored the "face" of DevShelf – everything you see and interact with. You learned how DevShelf shows you search results and takes your input, whether through a simple text command (CLI) or beautiful buttons and windows (GUI).

But what exactly is DevShelf *showing* you? What information makes up each book result on your screen? That's what we'll uncover in this chapter: the heart of our library, the **Book (Domain Model)**.

### What is a "Book" in DevShelf?

Imagine DevShelf as a digital library. What's the main thing a library stores? Books, of course! But in a computer program, a "book" isn't a physical object you can touch. Instead, it's a collection of information, a "data unit," that tells us everything important about one particular book.

Think of it like a **detailed index card** for each book in a physical library. Each card has spaces for the title, author, a short summary, and so on. In DevShelf, each `Book` object is one of these digital index cards. It holds all the essential details about a single book, making it easy for the application to display and search for them.

#### The Problem Our `Book` Solves

If you search for "Python" in DevShelf, the application needs to find specific books and show you their details. Without a clear way to represent *what* a book is and *what information it contains*, this would be impossible! The `Book` object gives us a standard way to store and share all that information throughout the application.

Let's look at an example use case: **Displaying book details after a search.**
When you type "Clean Code" and press Enter, DevShelf needs to:
1.  Find the "Clean Code" book.
2.  Grab all its details (title, author, description, rating, etc.).
3.  Show those details to you on the screen (which is the job of the UI we saw in Chapter 1!).

The `Book` domain model is the key to step 2!

### What Information Does a `Book` Hold?

Our `Book` "index card" has several important slots for information. These are called "properties" or "fields" in programming. Here are the main properties of a `Book` in DevShelf:

*   **`bookId`**: A unique number for each book. Like a library's unique call number.
*   **`title`**: The name of the book (e.g., "Clean Code").
*   **`author`**: Who wrote the book (e.g., "Robert C. Martin").
*   **`description`**: A short summary of what the book is about.
*   **`progLang`**: The main programming language discussed in the book (e.g., "Java", "Python").
*   **`category`**: The general topic of the book (e.g., "Algorithms", "Software Engineering").
*   **`tag`**: Keywords or topics associated with the book (e.g., ["clean-code", "refactoring"]). These help with more specific searches.
*   **`rating`**: How highly the book is rated (e.g., 4.6 out of 5).
*   **`coverUrl`**: A link to an image of the book's cover.
*   **`downLink`**: A link to download the book (if available).

### Getting Our Hands on `Book` Objects

Before DevShelf can display a book, it first needs to load the book's information into memory. This happens right when the application starts up. Think of it as opening the library and taking all the index cards out of their box, ready to be looked up.

The `BookLoader` is like the library assistant who takes all the physical books (or, in our case, a digital list of book details) and meticulously creates one index card (a `Book` object) for each.

Let's see how DevShelf gets these `Book` objects:

```mermaid
sequenceDiagram
    participant You
    participant Main App
    participant BookLoader
    participant book.json file

    You->>Main App: "Start DevShelf!"
    Main App->>BookLoader: "Load all book details"
    Note over BookLoader: Uses a special tool to read JSON
    BookLoader->>book.json file: "Read contents of book.json"
    book.json file-->>BookLoader: "Here's all the book data (as text)!"
    BookLoader->>BookLoader: "Convert text data into Book objects"
    BookLoader-->>Main App: "Here's a list of all Book objects!"
    Main App-->>You: "Books are loaded, ready for your search!"
```

This diagram shows the journey from starting the app to having all `Book` objects ready. The `BookLoader` does the heavy lifting of reading from a file named `book.json` and turning that raw text into usable `Book` objects.

#### The `Book` Class: Our Digital Index Card Blueprint

In Java, we define what a `Book` looks like using a `class`. This `Book` class acts like a blueprint for all our individual book objects. We use a helper library called Lombok to automatically create "get" and "set" methods, which are common ways to access and change the properties of a `Book` object.

**`src/main/java/domain/Book.java` (Simplified Blueprint)**
```java
// This is the blueprint for our digital "index card"
public class Book {
    private int bookId;         // Unique ID (e.g., 1)
    private String title;       // Name of the book (e.g., "Clean Code")
    private String author;      // Writer(s) (e.g., "Robert C. Martin")
    private String description; // A quick summary
    private float rating;       // How good it is (e.g., 4.6)
    private String coverUrl;    // Link to its cover image
    private String downLink;    // Link to download the PDF
    // ... other details like programming language and tags are also here!
    // (Lombok automatically adds 'get' and 'set' methods, and constructors
    // are special methods for creating new Book objects.)
}
```
In this code, each `private` line defines one of the properties we discussed. When DevShelf creates a `Book` object (using special methods called "constructors"), it fills these properties with actual information for a specific book.

#### Loading Books from a File: The `book.json`

All the book information for DevShelf is stored in a file called `book.json`. This is a special type of text file that stores data in a structured way that computers can easily understand. It's like a neatly organized digital list of all our book index cards.

Here's a tiny peek at what `book.json` looks like:

**`src/main/resources/data/book.json` (Snippet)**
```json
[
  {
    "bookId": 1,
    "title": "Introduction to Algorithms",
    "author": "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein",
    "description": "Comprehensive algorithms textbook...",
    "progLang": "Pseudo-code",
    "category": "Algorithms",
    "tag": [ "algorithms", "data-structures" ],
    "rating": 4.7,
    "coverUrl": "...",
    "downLink": "..."
  },
  {
    "bookId": 2,
    "title": "The Pragmatic Programmer",
    "author": "Andrew Hunt, David Thomas",
    "description": "Practical advice and philosophies...",
    "progLang": "Multiple",
    "category": "Software Engineering",
    "tag": [ "best-practices", "career" ],
    "rating": 4.6,
    "coverUrl": "...",
    "downLink": "..."
  }
]
```
As you can see, it's a list of book details, where each book's details match the properties we defined in our `Book` class blueprint.

The `BookLoader` class's job is to read this `book.json` file and turn its text into actual `Book` objects that our Java program can use.

**`src/main/java/storage/BookLoader.java` (Simplified `loadBooks` method)**
```java
package storage;

import com.fasterxml.jackson.core.type.TypeReference; // Helps read lists of objects
import com.fasterxml.jackson.databind.ObjectMapper; // The "magic tool" for JSON
import domain.Book; // Our Book blueprint
import java.io.IOException; // To handle errors when reading files
import java.io.InputStream; // To read from internal resources
import java.util.List; // To store a list of Book objects

public class BookLoader {
    private final ObjectMapper mapper = new ObjectMapper(); // Our JSON reading tool
    private final String resourcePath = "/data/book.json"; // Location of our book data

    // This method does the actual work of loading books
    public List<Book> loadBooks() {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            // The 'mapper' reads the data and magically turns it into a list of Book objects!
            return mapper.readValue(inputStream, new TypeReference<List<Book>>() {});
        } catch (IOException e) {
            // If something goes wrong (e.g., file not found), print an error
            System.err.println("❌ Error loading books: " + e.getMessage());
            return List.of(); // Return an empty list to avoid crashing (Java 9+ simplified)
        }
    }
}
```
Here's what happens in `BookLoader.loadBooks()`:
1.  It creates an `ObjectMapper`, which is a powerful "translator" that knows how to convert between Java objects and JSON text.
2.  It uses `getClass().getResourceAsStream(resourcePath)` to find and open our `book.json` file, which is packaged inside the application.
3.  The `mapper.readValue(...)` line is the key! It reads all the text from `book.json` and, because we tell it to expect a `List<Book>`, it magically creates a Java `List` filled with `Book` objects, each populated with the data from the JSON.
4.  If there's any problem (like the file not being there or being corrupted), it catches the error and prints a message, then returns an empty list to keep the application running smoothly.

#### Using Loaded Books in the Application

Once the `BookLoader` has done its job, the main part of the DevShelf application (which we'll explore in [Application Orchestration](03_application_orchestration_.md)) stores these `Book` objects. Typically, they're kept in a special collection (like a `Map` in Java) where each book's unique `bookId` is used to quickly find it. This collection is then passed to important parts of the application, like the search engine, so they can access book details when needed.

For example, when the search engine finds a `bookId` that matches your query, it can then easily fetch the full `Book` object from this map and help the UI display its details:

```java
// Simplified code snippet from the search engine
// Imagine 'allBooksMap' is a Map<Integer, Book> containing all our loaded books

// ... after a search, we get a list of relevant book IDs ...
// Let's say the search engine found Book with ID 3 (which is "Clean Code")
int foundBookId = 3;

// Retrieve the actual Book object using its ID
Book foundBook = allBooksMap.get(foundBookId);

if (foundBook != null) {
    // Now we can easily access its details!
    System.out.println("Title: " + foundBook.getTitle());
    System.out.println("Author: " + foundBook.getAuthor());
    System.out.println("Rating: " + foundBook.getRating());
    System.out.println("Download: " + foundBook.getDownLink());
    // The UI (from Chapter 1) will then take these details and show them nicely to the user
}
```
The output of this snippet would be something like:
```
Title: Clean Code
Author: Robert C. Martin
Rating: 4.6
Download: https://ptgmedia.pearsoncmg.com/images/9780132350884/samplepages/9780132350884.pdf
```
This is how a `Book` object, acting as our digital index card, helps DevShelf organize and present information effectively. It's the core data that the UI shows, and that the search engine works with.

### Conclusion

In this chapter, we unpacked the `Book` domain model, which is the core unit of data in our DevShelf library. We learned that:
*   Each `Book` object is like a detailed digital index card, holding all the essential information about a single book.
*   It has properties like `bookId`, `title`, `author`, `description`, `rating`, `coverUrl`, and more.
*   The `BookLoader` is responsible for reading book data from a `book.json` file and transforming it into usable `Book` objects when the application starts.
*   These `Book` objects are then stored in a way that allows other parts of the application, like the search engine, to easily find and retrieve their details.

Understanding the `Book` object is fundamental because it's what our users are ultimately searching for and interacting with through the UI. Now that we know what a book is and how DevShelf gets its book data ready, let's explore how all the different parts of DevShelf work together to make a functional application!

[Next Chapter: Application Orchestration](03_application_orchestration_.md)
