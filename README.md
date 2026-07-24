<h1 align="center">📚 DevShelf</h1>

<p align="center">
  <strong>Google, But Only for Computer Science Books</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/build-passing-success?style=for-the-badge" alt="Build Status" />
  <img src="https://img.shields.io/badge/Platform-Windows%20%7C%20Linux-blue?style=for-the-badge" alt="Platform" />
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License" />
<a href="https://deepwiki.com/qasimio/DevShelf">
    <img src="https://deepwiki.com/badge.svg" alt="Ask DeepWiki" />
  </a>
</p>

<p align="center">
  <strong>Try DevShelf locally — no cloud dependency required.</strong><br>
  <em>Offline-first • Fast • Built from first principles</em>
</p>

<br>

---

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

## 🎬 Walkthrough under a minute!

<p align="center">
  <a href="https://youtu.be/YOUR_VIDEO_ID">
    <img
      src="https://img.youtube.com/vi/YOUR_VIDEO_ID/maxresdefault.jpg"
      alt="Watch the DevShelf demo"
      width="800"
    />
  </a>
</p>

<p align="center">
  <strong>▶ Watch DevShelf search, rank, filter, and recommend Computer Science books in under one minute.</strong>
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

---

## 💖 Support

DevShelf is actively maintained and improved. If you find it useful or appreciate the engineering behind it, you can support its continued development and maintenance:

👉 https://patreon.com/qasimio

---

## 👥 Engineering Team

| Name | Role | Focus |
|------|------|-------|
| Qasim Sethar | Lead Architect | Core search engine, system architecture, ranking algorithms |
| Nancy Chawla | Frontend Engineer | JavaFX UI, UX design, view controllers |
| Ritika Lund | Feature Engineer | Recommendations, filtering logic, data analysis |

---

<a href="https://peerlist.io/qasimio/project/devshelf" target="_blank" rel="noreferrer">
				<img
					src="https://peerlist.io/api/v1/projects/embed/PRJH6A7AGANB76896CONO696R6PK6B?showUpvote=true&theme=dark"
					alt="DevShelf"
					style="width: auto; height: 72px;"
				/>
			</a>

---

Built by Qasim Sethar with pure Java, mathematics, and first principles.


