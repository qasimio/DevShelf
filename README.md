# 📘 DevShelf - Intelligent Library Search Engine

> **"Search is not just about matching keywords; it's about understanding relevance."**

DevShelf is a high-performance, offline-first desktop search engine for programming books. Unlike standard database queries, DevShelf utilizes **Information Retrieval (IR)** algorithms to rank books based on **Text Relevance** (TF-IDF), **Static Quality** (Ratings), and **Dynamic Popularity** (User Behavior).

Built with **Java 17** and **JavaFX**, featuring a strictly decoupled **MVC Architecture**.

---

## 🚀 Key Features

### 🧠 Intelligent Ranking ("The Master Formula")
DevShelf doesn't just find books; it ranks them using a weighted algorithm:
* **Vector Space Model:** Uses **TF-IDF** and **Cosine Similarity** to measure how relevant a book is to your query.
* **Learning to Rank:** An integrated Feedback Loop (`LogAnalyzer`) tracks user clicks to boost popular books over time.
* **Exact Match Boosting:** Intelligently prioritizes exact title matches.

### ⚡ Performance
* **O(1) Search Speed:** Utilizes a pre-compiled **Inverted Index** to deliver sub-millisecond results.
* **Real-Time Autocomplete:** A **Trie (Prefix Tree)** data structure predicts queries instantly as you type.

### 🔍 Discovery & UX
* **Graph-Based Recommendations:** A recommendation engine builds a network of books based on authors and categories to suggest "You Might Also Like."
* **Smart Typo Correction:** Uses Levenshtein distance to find the intended query (e.g., "Pythn" -> "Python").
* **Integrated PDF Reader:** Read books directly inside the app using a custom `WebView` wrapper.

---

## 🛠️ System Architecture

DevShelf follows a **Two-Part Architecture** to ensure maximum runtime performance:

1.  **The Offline Factory:** A backend process that ingests raw data, performs heavy calculations (Stemming, Vectorizing, Indexing), and generates optimized JSON artifacts.
2.  **The Online Storefront:** A lightweight JavaFX application that loads the pre-computed artifacts. It follows the **Model-View-Controller (MVC)** pattern with a **Service Facade** layer.

### Tech Stack
* **Language:** Java 17 (Temurin)
* **GUI:** JavaFX + CSS (Custom Modern Design)
* **Build Tool:** Maven (Shaded Fat-JAR)
* **Data Processing:** Jackson (JSON), Snowball Stemmer (NLP)

---

## 📸 Screenshots

| Home Screen (Trending) | Detail View & Graph |
|:---:|:---:|
| ![Home Screen](path/to/screenshot1.png) | ![Detail View](path/to/screenshot2.png) |

*(Note: Replace `path/to/screenshot.png` with actual image links)*

---

## 📥 Installation & Usage

**No Java Installation Required.** DevShelf is deployed as a portable application.

1.  Go to the [**Releases Page**](../../releases).
2.  Download **`DevShelf_Portable_v1.0.zip`**.
3.  Extract the zip file.
4.  Open the folder and run **`DevShelf.exe`**.

*Note: The application will create a folder in your `%APPDATA%/DevShelf` directory to store logs and learn from your usage.*

---

## 🏗️ Build from Source

If you want to modify the code or build it yourself:

1.  **Clone the repo**
    ```bash
    git clone [https://github.com/Kas-sim/DevShelf.git](https://github.com/Kas-sim/DevShelf.git)
    ```
2.  **Build the Index (Offline Step)**
    Run `core.IndexerMain` in your IDE. This generates `src/main/resources/data/index_data.json`.
3.  **Package the App**
    ```bash
    mvn clean package
    ```
4.  **Create Portable Image (Requires JDK 17 with jpackage)**
    ```bash
    jpackage --type app-image --name "DevShelf" --input target/ --main-jar DevShelf-1.0-SNAPSHOT.jar --main-class core.Launcher --dest target/release
    ```

---

## 👨‍💻 Project Structure

* `core`: Application entry points (`GuiMain`, `IndexerMain`).
* `features`: Pure logic modules (`search`, `recommendation`).
* `ui.gui`: Frontend logic (`controllers`, `services`, `fxml`).
* `storage`: Data persistence and loading.
* `domain`: Data models (POJOs).

---

**Developed by Kassim** *Built to explore Advanced Data Structures & Algorithms.*
