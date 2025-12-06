package features.search;

import java.util.*;
import java.util.stream.Collectors;

public class PhraseCompletion {

    private final TrieNode root = new TrieNode();

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();

        Set<String> associatedTitles = new HashSet<>();
    }


    public void insertWordAndTitle(String word, String fullTitle) {
        if (word == null || word.isEmpty()) return;

        TrieNode node = root;
        String lowerWord = word.toLowerCase();

        for (char c : lowerWord.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }

        node.associatedTitles.add(fullTitle);
    }

    public List<String> Complete(String prefix, int limit) {
        if (prefix == null || prefix.isEmpty() || limit <= 0) return Collections.emptyList();

        TrieNode node = root;
        String lowerPrefix = prefix.toLowerCase();

        for (char c : lowerPrefix.toCharArray()) {
            if (!node.children.containsKey(c)) return Collections.emptyList();
            node = node.children.get(c);
        }

        Set<String> titles = new HashSet<>();
        collectTitles(node, titles, limit);

        List<String> resultList = new ArrayList<>(titles);
        Collections.sort(resultList);

        return resultList.subList(0, Math.min(resultList.size(), limit));
    }

    public List<String> Complete(String prefix) {
        return Complete(prefix, 10);
    }


    private void collectTitles(TrieNode node, Set<String> titles, int limit) {
        if (titles.size() >= limit) return;

        titles.addAll(node.associatedTitles);

        if (titles.size() >= limit) return;

        List<Character> sortedKeys = new ArrayList<>(node.children.keySet());
        Collections.sort(sortedKeys);

        for (char key : sortedKeys) {
            TrieNode child = node.children.get(key);
            collectTitles(child, titles, limit);

            if (titles.size() >= limit) return;
        }
    }
}