package com.campuspulse.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Explainable Java Similarity Calculation Engine.
 * Does NOT use complex machine-learning or black-box libraries.
 * 
 * Formula:
 * 1. Category Match       = 40 points
 * 2. Location Match       = 30 points
 * 3. Keyword Overlap      = 20 points (Jaccard Index on cleaned tokens)
 * 4. Text Similarity      = 10 points (Token containment & character overlap)
 * ----------------------------------------------------
 * Total                   = 100 points
 * 
 * If Total Score >= 60, system flags: "A similar issue may already exist."
 */
public class TextSimilarityUtil {

    public static final int THRESHOLD = 60;

    // Common English stopwords to ignore during keyword analysis
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and",
        "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being",
        "below", "between", "both", "but", "by", "can't", "cannot", "could", "did",
        "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further",
        "had", "has", "have", "having", "he", "her", "here", "hers", "herself", "him",
        "himself", "his", "how", "i", "if", "in", "into", "is", "isn't", "it", "its",
        "itself", "let's", "me", "more", "most", "my", "myself", "no", "nor", "not",
        "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours",
        "ourselves", "out", "over", "own", "same", "she", "should", "so", "some",
        "such", "than", "that", "the", "their", "theirs", "them", "themselves", "then",
        "there", "these", "they", "this", "those", "through", "to", "too", "under",
        "until", "up", "very", "was", "wasn't", "we", "were", "what", "when", "where",
        "which", "while", "who", "whom", "why", "with", "won't", "would", "you", "your",
        "keeps", "continuously", "frequently", "getting", "please"
    ));

    /**
     * Holds the similarity evaluation breakdown.
     */
    public static class SimilarityResult {
        private final int totalScore;
        private final int categoryScore;
        private final int locationScore;
        private final int keywordScore;
        private final int textScore;
        private final boolean isSimilar;

        public SimilarityResult(int categoryScore, int locationScore, int keywordScore, int textScore) {
            this.categoryScore = categoryScore;
            this.locationScore = locationScore;
            this.keywordScore = keywordScore;
            this.textScore = textScore;
            this.totalScore = categoryScore + locationScore + keywordScore + textScore;
            this.isSimilar = this.totalScore >= THRESHOLD;
        }

        public int getTotalScore() { return totalScore; }
        public int getCategoryScore() { return categoryScore; }
        public int getLocationScore() { return locationScore; }
        public int getKeywordScore() { return keywordScore; }
        public int getTextScore() { return textScore; }
        public boolean isSimilar() { return isSimilar; }
    }

    /**
     * Computes similarity between a newly submitted complaint and an existing issue or complaint.
     */
    public static SimilarityResult calculate(
            int newCategoryId, int existingCategoryId,
            int newLocationId, int existingLocationId,
            String newText, String existingText) {

        int catScore = 0;
        int locScore = 0;
        int keywordScore = 0;
        int textScore = 0;

        // 1. Category Match (40 pts)
        if (newCategoryId > 0 && newCategoryId == existingCategoryId) {
            catScore = 40;
        }

        // 2. Location Match (30 pts)
        if (newLocationId > 0 && newLocationId == existingLocationId) {
            locScore = 30;
        }

        // Tokenize and clean text
        Set<String> tokens1 = extractKeywords(newText);
        Set<String> tokens2 = extractKeywords(existingText);

        // 3. Keyword Overlap (20 pts) using Jaccard Similarity Index
        if (!tokens1.isEmpty() && !tokens2.isEmpty()) {
            Set<String> intersection = new HashSet<>(tokens1);
            intersection.retainAll(tokens2);

            Set<String> union = new HashSet<>(tokens1);
            union.addAll(tokens2);

            double jaccard = (double) intersection.size() / union.size();
            keywordScore = (int) Math.round(jaccard * 20.0);
        }

        // 4. Basic Text & Substring Overlap (10 pts)
        if (newText != null && existingText != null) {
            String s1 = newText.trim().toLowerCase();
            String s2 = existingText.trim().toLowerCase();

            if (s1.equals(s2)) {
                textScore = 10;
            } else if (s1.contains(s2) || s2.contains(s1)) {
                textScore = 8;
            } else {
                // Check common token count
                int matches = 0;
                for (String t : tokens1) {
                    if (s2.contains(t)) matches++;
                }
                if (!tokens1.isEmpty()) {
                    textScore = Math.min(10, (int) Math.round(((double) matches / tokens1.size()) * 10.0));
                }
            }
        }

        return new SimilarityResult(catScore, locScore, keywordScore, textScore);
    }

    private static Set<String> extractKeywords(String text) {
        Set<String> words = new HashSet<>();
        if (text == null) return words;

        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] parts = cleaned.split("\\s+");

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.length() > 2 && !STOP_WORDS.contains(trimmed)) {
                words.add(trimmed);
            }
        }
        return words;
    }
}
