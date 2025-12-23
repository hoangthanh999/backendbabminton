package com.badminton.repository.post;

import com.badminton.entity.post.Post;
import com.badminton.enums.PostStatus;
import com.badminton.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    // Basic Queries
    Optional<Post> findBySlug(String slug);

    Optional<Post> findByIdAndDeletedAtIsNull(Long id);

    Optional<Post> findBySlugAndDeletedAtIsNull(String slug);

    boolean existsBySlug(String slug);

    // Author Queries
    List<Post> findByAuthorId(Long authorId);

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    List<Post> findByAuthorIdAndStatus(Long authorId, PostStatus status);

    Page<Post> findByAuthorIdAndStatusAndDeletedAtIsNull(Long authorId, PostStatus status, Pageable pageable);

    // Category Queries
    List<Post> findByCategoryId(Long categoryId);

    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    Page<Post> findPublishedPostsByCategory(@Param("categoryId") Long categoryId,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    // Status Queries
    List<Post> findByStatus(PostStatus status);

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    List<Post> findByStatusAndDeletedAtIsNull(PostStatus status);

    Page<Post> findByStatusAndDeletedAtIsNull(PostStatus status, Pageable pageable);

    // Type Queries
    List<Post> findByPostType(PostType postType);

    Page<Post> findByPostType(PostType postType, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.postType = :postType " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    Page<Post> findPublishedPostsByType(@Param("postType") PostType postType,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    // Published Posts
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    Page<Post> findAllPublishedPosts(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL")
    List<Post> findAllPublishedPosts(@Param("now") LocalDateTime now);

    // Featured Posts
    @Query("SELECT p FROM Post p WHERE p.isFeatured = true " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findFeaturedPosts(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isFeatured = true " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.displayOrder, p.publishedAt DESC")
    List<Post> findFeaturedPostsOrdered(@Param("now") LocalDateTime now);

    // Pinned Posts
    @Query("SELECT p FROM Post p WHERE p.isPinned = true " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.displayOrder, p.publishedAt DESC")
    List<Post> findPinnedPosts(@Param("now") LocalDateTime now);

    // Trending Posts
    @Query("SELECT p FROM Post p WHERE p.isTrending = true " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.viewCount DESC, p.publishedAt DESC")
    List<Post> findTrendingPosts(@Param("now") LocalDateTime now, Pageable pageable);

    // Scheduled Posts
    @Query("SELECT p FROM Post p WHERE p.status = 'SCHEDULED' " +
            "AND p.scheduledAt <= :now " +
            "AND p.deletedAt IS NULL")
    List<Post> findPostsToPublish(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Post p WHERE p.status = 'SCHEDULED' " +
            "AND p.scheduledAt > :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.scheduledAt")
    List<Post> findScheduledPosts(@Param("now") LocalDateTime now);

    // Search Queries
    @Query("SELECT p FROM Post p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL")
    Page<Post> searchPublishedPosts(@Param("keyword") String keyword,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.deletedAt IS NULL")
    Page<Post> searchAllPosts(@Param("keyword") String keyword, Pageable pageable);

    // Date Range Queries
    List<Post> findByPublishedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
            "AND p.publishedAt BETWEEN :startDate AND :endDate " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findPublishedPostsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Popular Posts (by views)
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.viewCount DESC")
    List<Post> findMostViewedPosts(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
            "AND p.publishedAt BETWEEN :startDate AND :endDate " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.viewCount DESC")
    List<Post> findMostViewedPostsInPeriod(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Popular Posts (by engagement)
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY (p.likeCount + p.commentCount + p.shareCount) DESC")
    List<Post> findMostEngagedPosts(@Param("now") LocalDateTime now, Pageable pageable);

    // Related Posts
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId " +
            "AND p.id != :postId " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findRelatedPostsByCategory(@Param("postId") Long postId,
            @Param("categoryId") Long categoryId,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    // Recent Posts
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findRecentPosts(@Param("now") LocalDateTime now, Pageable pageable);

    // Draft Posts
    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId " +
            "AND p.status = 'DRAFT' " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.updatedAt DESC")
    List<Post> findDraftsByAuthor(@Param("authorId") Long authorId);

    // Pending Review
    @Query("SELECT p FROM Post p WHERE p.status = 'PENDING' " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.createdAt")
    List<Post> findPendingReviewPosts();

    // Statistics
    @Query("SELECT COUNT(p) FROM Post p WHERE p.status = :status " +
            "AND p.deletedAt IS NULL")
    long countByStatus(@Param("status") PostStatus status);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.author.id = :authorId " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.deletedAt IS NULL")
    long countPublishedPostsByAuthor(@Param("authorId") Long authorId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.category.id = :categoryId " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.deletedAt IS NULL")
    long countPublishedPostsByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT SUM(p.viewCount) FROM Post p WHERE p.author.id = :authorId " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.deletedAt IS NULL")
    Long sumViewCountByAuthor(@Param("authorId") Long authorId);

    // Archive
    @Query("SELECT YEAR(p.publishedAt), MONTH(p.publishedAt), COUNT(p) " +
            "FROM Post p " +
            "WHERE p.status = 'PUBLISHED' " +
            "AND p.deletedAt IS NULL " +
            "GROUP BY YEAR(p.publishedAt), MONTH(p.publishedAt) " +
            "ORDER BY YEAR(p.publishedAt) DESC, MONTH(p.publishedAt) DESC")
    List<Object[]> findPostArchive();

    @Query("SELECT p FROM Post p WHERE YEAR(p.publishedAt) = :year " +
            "AND MONTH(p.publishedAt) = :month " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findPostsByYearMonth(@Param("year") Integer year, @Param("month") Integer month);

    // Language
    List<Post> findByLanguage(String language);

    @Query("SELECT p FROM Post p WHERE p.language = :language " +
            "AND p.status = 'PUBLISHED' " +
            "AND p.publishedAt <= :now " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.publishedAt DESC")
    Page<Post> findPublishedPostsByLanguage(@Param("language") String language,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    // Translations
    List<Post> findByTranslationOf(Long translationOf);

    // Update Operations
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.shareCount = p.shareCount + 1 WHERE p.id = :postId")
    void incrementShareCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount > 0")
    void decrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1 WHERE p.id = :postId")
    void incrementCommentCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount - 1 WHERE p.id = :postId AND p.commentCount > 0")
    void decrementCommentCount(@Param("postId") Long postId);

    // Bulk Operations
    @Modifying
    @Query("UPDATE Post p SET p.status = :newStatus WHERE p.id IN :postIds")
    void bulkUpdateStatus(@Param("postIds") List<Long> postIds, @Param("newStatus") PostStatus newStatus);

    @Modifying
    @Query("UPDATE Post p SET p.deletedAt = :now WHERE p.id IN :postIds")
    void bulkSoftDelete(@Param("postIds") List<Long> postIds, @Param("now") LocalDateTime now);
}
