# Tutorial: DevShelf

DevShelf is a **digital library search engine** that helps users *discover and read programming books*.
It intelligently **searches for books** using a pre-built index, provides "Did you mean?" suggestions, and offers
*personalized recommendations* based on book details and user popularity. The system also tracks user clicks
to continuously improve its search results and recommendations, presenting information through both command-line
and graphical interfaces.


## Visual Overview

```mermaid
flowchart TD
    A0["Book (Domain Model)
"]
    A1["Application Orchestration
"]
    A2["User Interface (UI) Presentation
"]
    A3["Offline Search Indexing
"]
    A4["Core Search Engine
"]
    A5["User Behavior Analytics
"]
    A6["Intelligent Search Enhancements
"]
    A7["Text Preprocessing
"]
    A1 -- "Loads" --> A0
    A1 -- "Loads Index Data for" --> A3
    A1 -- "Sets up" --> A2
    A1 -- "Initializes" --> A4
    A1 -- "Initializes" --> A5
    A1 -- "Initializes" --> A6
    A1 -- "Prepares tools for" --> A7
    A3 -- "Indexes" --> A0
    A3 -- "Uses" --> A7
    A3 -- "Provides Index Data to" --> A4
    A4 -- "Processes Queries with" --> A7
    A4 -- "Provides Initial Results to" --> A6
    A5 -- "Logs Clicks on" --> A0
    A5 -- "Provides Popularity to" --> A6
    A6 -- "Enhances" --> A2
    A2 -- "Sends Query to" --> A4
```

## Chapters

1. [User Interface (UI) Presentation
](01_user_interface__ui__presentation_.md)
2. [Book (Domain Model)
](02_book__domain_model__.md)
3. [Application Orchestration
](03_application_orchestration_.md)
4. [Core Search Engine
](04_core_search_engine_.md)
5. [Text Preprocessing
](05_text_preprocessing_.md)
6. [Offline Search Indexing
](06_offline_search_indexing_.md)
7. [Intelligent Search Enhancements
](07_intelligent_search_enhancements_.md)
8. [User Behavior Analytics
](08_user_behavior_analytics_.md)
