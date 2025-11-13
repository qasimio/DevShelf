package features.recommendation;

import domain.Book;

import java.util.*;

public class Graph {
    public final Map<String, List<String>> adjList = new HashMap<>();
    public final Map<String, Integer> titleToId = new HashMap<>(); // For popularity lookup

    private String normalize(String title) {
        return (title == null) ? "" : title.toLowerCase().trim();
    }

    // --- Build the relationship graph ---
    public void buildGraph(List<Book> books) {
        if (books == null) return;

        for (Book b1 : books) {
            if (b1 == null || b1.getTitle() == null) continue;
            String title1 = normalize(b1.getTitle());
            adjList.putIfAbsent(title1, new ArrayList<>());
            titleToId.put(title1, b1.getBookId());

            for (Book b2 : books) {
                if (b2 == null || b2.getTitle() == null || b1.equals(b2)) continue;

                if (areRelated(b1, b2)) {
                    String title2 = normalize(b2.getTitle());
                    List<String> neighbors = adjList.get(title1);
                    if (!neighbors.contains(title2)) {
                        neighbors.add(title2);
                    }
                }
            }
        }
    }

    // --- Check if two books are related ---
    public boolean areRelated(Book b1, Book b2) {
        if (b1 == null || b2 == null) return false;

        // Check author
        if (b1.getAuthor() != null && b1.getAuthor().equalsIgnoreCase(b2.getAuthor()))
            return true;

        // Check category
        if (b1.getCategory() != null && b1.getCategory().equalsIgnoreCase(b2.getCategory()))
            return true;

        // Check tags
        if (b1.getTag() != null && b2.getTag() != null) {
            for (String tag1 : b1.getTag()) {
                if (tag1 == null) continue;
                for (String tag2 : b2.getTag()) {
                    if (tag2 == null) continue;
                    if (tag1.trim().equalsIgnoreCase(tag2.trim())) return true;
                }
            }
        }

        // Check programming language
        if (b1.getProgLang() != null && b1.getProgLang().equalsIgnoreCase(b2.getProgLang()))
            return true;

        return false;
    }

    // --- Get direct connections of a book ---
    public List<String> getConnections(String title) {
        return adjList.getOrDefault(normalize(title), new ArrayList<>());
    }

    // --- Print the graph for debugging ---
    public void printGraph() {
        for (String title : adjList.keySet()) {
            System.out.println(title + " -> " + adjList.get(title));
        }
    }

    // --- BFS-based recommendations ---
    public List<String> recommendBooks(String startTitle, int maxRecommendations) {
        if (startTitle == null || !adjList.containsKey(normalize(startTitle))) return Collections.emptyList();

        String start = normalize(startTitle);
        List<String> recommendations = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty() && recommendations.size() < maxRecommendations) {
            String current = queue.poll();
            for (String neighbor : adjList.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    recommendations.add(neighbor);
                    if (recommendations.size() >= maxRecommendations) break;
                }
            }
        }

        return recommendations;
    }

    // --- Popularity-aware recommendations ---
    public List<String> recommendPopularBooks(String bookTitle, int limit, Map<Integer, Double> popularityMap, List<Book> allBooks) {
        if (bookTitle == null || !adjList.containsKey(normalize(bookTitle))) return Collections.emptyList();

        String key = normalize(bookTitle);
        List<String> relatedBooks = new ArrayList<>(adjList.getOrDefault(key, Collections.emptyList()));

        relatedBooks.sort((a, b) -> {
            int idA = titleToId.getOrDefault(a, -1);
            int idB = titleToId.getOrDefault(b, -1);
            double popA = popularityMap != null ? popularityMap.getOrDefault(idA, 0.0) : 0.0;
            double popB = popularityMap != null ? popularityMap.getOrDefault(idB, 0.0) : 0.0;
            return Double.compare(popB, popA);
        });

        return relatedBooks.subList(0, Math.min(limit, relatedBooks.size()));
    }
}
