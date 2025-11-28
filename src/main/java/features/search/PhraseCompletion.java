package features.search;

import java.util.*;
import java.util.stream.Collectors;

public class PhraseCompletion {

    private final TrieNode root = new TrieNode();

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();

        // Stores all unique full titles that contain the word ending at this node.
        Set<String> associatedTitles = new HashSet<>();
    }

    // ------------------------------------------------------------------
    // Insertion: Inserts a single word and associates it with the full title.
    // ------------------------------------------------------------------

    /**
     * Inserts a single word into the Trie and links it to the full source title.
     * This method must be called for EVERY word in a book's title (in the Service Layer).
     * @param word The word to be indexed (e.g., "clean").
     * @param fullTitle The complete, original book title (e.g., "The Clean Code Handbook").
     */
    public void insertWordAndTitle(String word, String fullTitle) {
        if (word == null || word.isEmpty()) return;

        TrieNode node = root;
        String lowerWord = word.toLowerCase();

        // 1. Traverse or create path for the word
        for (char c : lowerWord.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }

        // 2. Associate the full title at the node where the word ends
        node.associatedTitles.add(fullTitle);
    }

    // ------------------------------------------------------------------
    // Completion: Finds full titles associated with words matching the prefix.
    // ------------------------------------------------------------------

    /**
     * Finds unique full titles where an indexed word starts with the given prefix.
     * Results are sorted alphabetically and limited by the count.
     * * @param prefix The search term prefix (e.g., "cle").
     * @param limit The maximum number of suggestions to return.
     * @return A list of unique, full book titles.
     */
    public List<String> Complete(String prefix, int limit) {
        if (prefix == null || prefix.isEmpty() || limit <= 0) return Collections.emptyList();

        TrieNode node = root;
        String lowerPrefix = prefix.toLowerCase();

        // 1. Traverse to the node representing the end of the prefix
        for (char c : lowerPrefix.toCharArray()) {
            if (!node.children.containsKey(c)) return Collections.emptyList();
            node = node.children.get(c);
        }

        // 2. Collect all unique associated titles from this node onwards
        Set<String> titles = new HashSet<>();
        collectTitles(node, titles, limit);

        // 3. Sort the unique titles alphabetically and apply the hard limit
        List<String> resultList = new ArrayList<>(titles);
        Collections.sort(resultList);

        // Use subList to return only the limited number of results
        return resultList.subList(0, Math.min(resultList.size(), limit));
    }

    // Overloaded method for convenience (uses a default limit of 10)
    public List<String> Complete(String prefix) {
        return Complete(prefix, 10);
    }


    // ------------------------------------------------------------------
    // DFS Helper: Recursively collects associated titles.
    // ------------------------------------------------------------------

    /**
     * DFS to collect all associated titles under this node, respecting the limit.
     */
    private void collectTitles(TrieNode node, Set<String> titles, int limit) {
        // Stop condition 1: Stop exploring if we've already collected enough titles
        if (titles.size() >= limit) return;

        // 1. Add all titles associated with the word ending at this node
        titles.addAll(node.associatedTitles);

        // Stop condition 2: Check limit again after adding titles
        if (titles.size() >= limit) return;

        // 2. Recurse through children
        // Sorting keys ensures a consistent collection order
        List<Character> sortedKeys = new ArrayList<>(node.children.keySet());
        Collections.sort(sortedKeys);

        for (char key : sortedKeys) {
            TrieNode child = node.children.get(key);
            collectTitles(child, titles, limit);

            // Re-check stop condition after recursion
            if (titles.size() >= limit) return;
        }
    }
}