package com.badminton.entity.post;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.PostStatus;
import com.badminton.enums.PostType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts", uniqueConstraints = @UniqueConstraint(name = "uk_slug", columnNames = "slug"), indexes = {
        @Index(name = "idx_category_status", columnList = "category_id, status"),
        @Index(name = "idx_author_status", columnList = "author_id, status"),
        @Index(name = "idx_published", columnList = "published_at"),
        @Index(name = "idx_featured", columnList = "is_featured, published_at"),
        @Index(name = "idx_type_status", columnList = "post_type, status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE posts SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Post extends AuditableEntity {

    // Core Info
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private PostCategory category;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "slug", unique = true, nullable = false, length = 500)
    private String slug;

    @Column(name = "excerpt", length = 1000)
    private String excerpt;

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false, length = 20)
    @Builder.Default
    private PostType postType = PostType.ARTICLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PostStatus status = PostStatus.DRAFT;

    // Media
    @Column(name = "featured_image")
    private String featuredImage;

    @Column(name = "featured_image_caption")
    private String featuredImageCaption;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "gallery", columnDefinition = "JSON")
    private String gallery; // JSON array of images

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "audio_url")
    private String audioUrl;

    // SEO
    @Column(name = "meta_title", length = 200)
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    @Column(name = "canonical_url")
    private String canonicalUrl;

    // Publishing
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by")
    private User publishedBy;

    // Display Options
    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_pinned")
    @Builder.Default
    private Boolean isPinned = false;

    @Column(name = "is_trending")
    @Builder.Default
    private Boolean isTrending = false;

    @Column(name = "allow_comments")
    @Builder.Default
    private Boolean allowComments = true;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    // Reading
    @Column(name = "reading_time")
    private Integer readingTime; // in minutes

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "share_count")
    @Builder.Default
    private Long shareCount = 0L;

    // Tags
    @Column(name = "tags", columnDefinition = "JSON")
    private String tags; // JSON array

    // Related Content
    @Column(name = "related_posts", columnDefinition = "JSON")
    private String relatedPosts; // JSON array of post IDs

    // Source
    @Column(name = "source_name", length = 200)
    private String sourceName;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "external_link")
    private String externalLink;

    // Language
    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "vi";

    @Column(name = "translation_of")
    private Long translationOf; // Parent post ID if this is a translation

    // Version Control
    @Column(name = "revision_number")
    @Builder.Default
    private Integer revisionNumber = 1;

    @Column(name = "last_edited_by_id")
    private Long lastEditedById;

    @Column(name = "last_edited_at")
    private LocalDateTime lastEditedAt;

    // Engagement
    @Column(name = "like_count")
    @Builder.Default
    private Long likeCount = 0L;

    @Column(name = "comment_count")
    @Builder.Default
    private Long commentCount = 0L;

    // Additional Info
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Calculated Fields
    @Formula("(SELECT COUNT(*) FROM post_comments pc WHERE pc.post_id = id AND pc.parent_id IS NULL)")
    private Integer topLevelCommentCount;

    @Formula("(SELECT AVG(CASE WHEN pl.is_like THEN 1 ELSE 0 END) FROM post_likes pl WHERE pl.post_id = id)")
    private Double likeRatio;

    // Relationships
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    @Builder.Default
    private Set<PostComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PostLike> likes = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Builder.Default
    private Set<PostTag> postTags = new HashSet<>();

    // Lifecycle
    @PrePersist
    public void prePersist() {
        if (slug == null && title != null) {
            slug = generateSlug(title);
        }

        if (excerpt == null && content != null) {
            excerpt = generateExcerpt(content);
        }

        if (readingTime == null && content != null) {
            readingTime = calculateReadingTime(content);
        }

        if (metaTitle == null) {
            metaTitle = title;
        }

        if (metaDescription == null && excerpt != null) {
            metaDescription = excerpt;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.revisionNumber++;
        this.lastEditedAt = LocalDateTime.now();
    }

    // Helper Methods

    /**
     * Generate slug from title
     */
    private String generateSlug(String title) {
        return title.toLowerCase()
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
                .trim();
    }

    /**
     * Generate excerpt from content
     */
    private String generateExcerpt(String content) {
        // Remove HTML tags
        String plainText = content.replaceAll("<[^>]*>", "");

        // Take first 200 characters
        if (plainText.length() > 200) {
            return plainText.substring(0, 197) + "...";
        }

        return plainText;
    }

    /**
     * Calculate reading time
     */
    private Integer calculateReadingTime(String content) {
        // Remove HTML tags
        String plainText = content.replaceAll("<[^>]*>", "");

        // Average reading speed: 200 words per minute
        int wordCount = plainText.split("\\s+").length;
        int minutes = (int) Math.ceil(wordCount / 200.0);

        return Math.max(1, minutes);
    }

    /**
     * Publish post
     */
    public void publish(User publisher) {
        if (status == PostStatus.PUBLISHED) {
            throw new IllegalStateException("Post is already published");
        }

        this.status = PostStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.publishedBy = publisher;
    }

    /**
     * Unpublish post
     */
    public void unpublish() {
        if (status != PostStatus.PUBLISHED) {
            throw new IllegalStateException("Only published posts can be unpublished");
        }

        this.status = PostStatus.DRAFT;
    }

    /**
     * Schedule post
     */
    public void schedule(LocalDateTime scheduledTime) {
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Scheduled time must be in the future");
        }

        this.status = PostStatus.SCHEDULED;
        this.scheduledAt = scheduledTime;
    }

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * Increment share count
     */
    public void incrementShareCount() {
        this.shareCount++;
    }

    /**
     * Add comment
     */
    public void addComment(PostComment comment) {
        comments.add(comment);
        comment.setPost(this);
        this.commentCount++;
    }

    /**
     * Remove comment
     */
    public void removeComment(PostComment comment) {
        comments.remove(comment);
        comment.setPost(null);
        this.commentCount--;
    }

    /**
     * Add like
     */
    public void addLike(PostLike like) {
        likes.add(like);
        like.setPost(this);
        this.likeCount++;
    }

    /**
     * Remove like
     */
    public void removeLike(PostLike like) {
        likes.remove(like);
        like.setPost(null);
        this.likeCount--;
    }

    /**
     * Check if published
     */
    public boolean isPublished() {
        return status == PostStatus.PUBLISHED &&
                publishedAt != null &&
                !publishedAt.isAfter(LocalDateTime.now());
    }

    /**
     * Check if scheduled
     */
    public boolean isScheduled() {
        return status == PostStatus.SCHEDULED &&
                scheduledAt != null &&
                scheduledAt.isAfter(LocalDateTime.now());
    }

    /**
     * Check if should be published
     */
    public boolean shouldBePublished() {
        return status == PostStatus.SCHEDULED &&
                scheduledAt != null &&
                !scheduledAt.isAfter(LocalDateTime.now());
    }

    /**
     * Get age in days
     */
    public Long getAgeInDays() {
        if (publishedAt == null) {
            return null;
        }

        return java.time.temporal.ChronoUnit.DAYS
                .between(publishedAt.toLocalDate(), LocalDateTime.now().toLocalDate());
    }

    /**
     * Calculate engagement rate
     */
    public BigDecimal getEngagementRate() {
        if (viewCount == 0) {
            return java.math.BigDecimal.ZERO;
        }

        long totalEngagements = likeCount + commentCount + shareCount;

        return java.math.BigDecimal.valueOf(totalEngagements)
                .divide(java.math.BigDecimal.valueOf(viewCount), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new java.math.BigDecimal("100"));
    }

    /**
     * Add tag
     */
    public void addTag(PostTag tag) {
        postTags.add(tag);
        tag.getPosts().add(this);
    }

    /**
     * Remove tag
     */
    public void removeTag(PostTag tag) {
        postTags.remove(tag);
        tag.getPosts().remove(this);
    }

    /**
     * Get full URL
     */
    public String getFullUrl(String baseUrl) {
        return baseUrl + "/posts/" + slug;
    }
}
