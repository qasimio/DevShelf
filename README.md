# 📚 DevShelf
## Search Engine that Gets Smarter with Every Search

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Build Status](https://img.shields.io/badge/build-passing-success?style=for-the-badge)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

---

<p align="center">
  <strong>Try DevShelf locally — no cloud dependency required.</strong><br>
  <em>Offline-first • Fast • Built from first principles</em>
</p>

<br>

<p align="center">
  <a href="https://github.com/qasimio/DevShelf/releases/latest/download/DevShelf-Setup.exe">
    <img
      src="https://img.shields.io/badge/⬇%20Download%20for%20Windows-0A66C2?style=for-the-badge&logo=windows&logoColor=white"
      alt="Download DevShelf for Windows"
      height="42"
    />
  </a>
</p>

<p align="center">
  <sub>
    Linux users: download the latest release from the same page.
  </sub>
</p>

---

## 📖 Overview

**DevShelf** is a high-performance vertical search engine for **Computer Science textbooks**.

Unlike traditional library software, DevShelf is built from **first principles** using a custom **Positional Inverted Index**, enabling **O(1)** query-time lookups without relying on Lucene, Elasticsearch, or external IR frameworks.

The system is designed for:

- Speed
- Precision
- Offline-first usage
- Cloud-synced freshness

---

## ⚡ Engineering Philosophy

DevShelf addresses the **Information Retrieval (IR)** problem at a local scale with production-grade constraints.

### Design Goals

1. **Fast**  
   Sub-millisecond query latency using optimized data structures.

2. **Smart**  
   Ranking goes beyond keyword matching by combining:
   - TF-IDF
   - Vector Space Models
   - Behavioral analytics

3. **Distributed by Design**  
   Index and metadata are fetched from a lightweight serverless source (GitHub Raw), allowing users to receive updated data without application updates.

---

## 🏗 System Architecture

DevShelf follows **Domain-Driven Design (DDD)** principles.

The system is divided into two major layers:

### Offline Indexing Layer
- Parses `books.json`
- Builds the inverted index
- Analyzes interaction logs
- Produces popularity vectors

### Online Query Engine
- Accepts user queries via CLI or JavaFX GUI
- Processes queries (tokenization, fuzzy matching, autocomplete)
- Ranks results using hybrid scoring
- Returns sorted documents

---

## 🧠 Ranking Model

Search relevance is computed using a weighted hybrid score:

Score(d, q) =
0.6 × TF-IDF  
0.2 × Popularity  
0.2 × Rating  

### Ranking Signals

| Signal      | Description |
|------------|-------------|
| TF-IDF     | Statistical importance of query terms |
| Popularity | Derived from offline click and usage logs |
| Rating     | Quality signals embedded in the dataset |

---

## 🚀 Key Features

### Core Search Engine

- Custom inverted index for constant-time term lookup
- Trie-based autocomplete with linear time complexity
- Fuzzy matching using Levenshtein distance for typo tolerance

### Intelligent Features

- Recommendation graph based on category overlap and usage patterns
- Dynamic filtering by relevance, popularity, year, and rating
- Memory-mapped caching for frequently accessed index segments

### Cloud Sync

- Automatically fetches the latest index and metadata on startup
- Feedback pipeline captures missing content requests

---

## 📥 Installation

### For Users (Windows)

1. Open the Releases page
2. Download `DevShelf-Setup.exe`
3. Run the installer
4. Launch the application

---

### For Developers

DevShelf is a Maven-based Java project.

```bash

git clone https://github.com/qasimio/DevShelf.git
cd DevShelf
mvn clean install
mvn javafx:run

```

## 👥 Engineering Team

| Name | Role | Focus |
|------|------|-------|
| Muhammad Qasim | Lead Architect | Core search engine, system architecture, ranking algorithms |
| Nancy Chawla | Frontend Engineer | JavaFX UI, UX design, view controllers |
| Ritika Lund | Feature Engineer | Recommendations, filtering logic, data analysis |

---

Built with pure Java, mathematics, and first principles.


