package com.badminton.util;

import com.badminton.entity.post.Post;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostUtils {

    /**
     * Generate unique slug
     */
    public static String generateUniqueSlug(String title, Long postId) {
        String baseSlug = generateSlug(title);

        if (postId != null) {
            return baseSlug + "-" + postId;
        }

        return baseSlug;
    }

    /**
     * Generate slug from text
     */
    public static String generateSlug(String text) {
        return text.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "")
                .trim();
    }

    /**
     * Extract first image from content
     */
    public static String extractFirstImage(String htmlContent) {
        Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(htmlContent);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    /**
     * Strip HTML tags
     */
    public static String stripHtml(String html) {
        if (html == null) {
            return null;
        }

        return html.replaceAll("<[^>]*>", "")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&quot;", "\"")
                .trim();
    }

    /**
     * Calculate reading time
     */
    public static int calculateReadingTime(String content) {
        String plainText = stripHtml(content);
        int wordCount = plainText.split("\\s+").length;

        // Average reading speed: 200 words per minute
        int minutes = (int) Math.ceil(wordCount / 200.0);

        return Math.max(1, minutes);
    }

    /**
     * Generate excerpt
     */
    public static String generateExcerpt(String content, int maxLength) {
        String plainText = stripHtml(content);

        if (plainText.length() <= maxLength) {
            return plainText;
        }

        // Find last space before maxLength
        int lastSpace = plainText.lastIndexOf(' ', maxLength - 3);

        if (lastSpace > 0) {
            return plainText.substring(0, lastSpace) + "...";
        }

        return plainText.substring(0, maxLength - 3) + "...";
    }

    /**
     * Check if post should be auto-published
     */
    public static boolean shouldAutoPublish(Post post) {
        return post.getStatus() == com.badminton.enums.PostStatus.SCHEDULED &&
                post.getScheduledAt() != null &&
                !post.getScheduledAt().isAfter(LocalDateTime.now());
    }

    /**
     * Sanitize HTML content
     */
    public static String sanitizeHtml(String html) {
        if (html == null) {
            return null;
        }

        // Remove dangerous tags and attributes
        return html.replaceAll("<script[^>]*>.*?</script>", "")
                .replaceAll("<iframe[^>]*>.*?</iframe>", "")
                .replaceAll("javascript:", "")
                .replaceAll("on\\w+=\"[^\"]*\"", "")
                .replaceAll("on\\w+='[^']*'", "");
    }

    /**
     * Extract keywords from content
     */
    public static String[] extractKeywords(String content, int maxKeywords) {
        String plainText = stripHtml(content).toLowerCase();

        // Remove common words (stop words)
        String[] stopWords = { "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for" };

        for (String stopWord : stopWords) {
            plainText = plainText.replaceAll("\\b" + stopWord + "\\b", "");
        }

        // Split into words and count frequency
        String[] words = plainText.split("\\s+");
        java.util.Map<String, Integer> wordFreq = new java.util.HashMap<>();

        for (String word : words) {
            if (word.length() > 3) {
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }

        // Sort by frequency and return top keywords
        return wordFreq.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(maxKeywords)
                .map(java.util.Map.Entry::getKey)
                .toArray(String[]::new);
    }

    /**
     * Format view count for display
     */
    public static String formatViewCount(Long viewCount) {
        if (viewCount == null || viewCount == 0) {
            return "0";
        }

        if (viewCount < 1000) {
            return viewCount.toString();
        } else if (viewCount < 1000000) {
            return String.format("%.1fK", viewCount / 1000.0);
        } else {
            return String.format("%.1fM", viewCount / 1000000.0);
        }
    }
}
