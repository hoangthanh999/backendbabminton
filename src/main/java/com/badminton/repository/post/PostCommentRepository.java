package com.badminton.repository.post;

import com.badminton.entity.post.PostComment;
import com.badminton.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // Basic Queries
    List<PostComment> findByPostId(Long postId);

    Page<PostComment> findByPostId(Long postId, Pageable pageable);

    List<PostComment> findByUserId(Long userId);

    Page<PostComment> findByUserId(Long userId, Pageable pageable);

    List<PostComment> findByPostIdAndDeletedAtIsNull(Long postId);

    // Status Queries
    List<PostComment> findByStatus(CommentStatus status);

    Page<PostComment> findByStatus(CommentStatus status, Pageable pageable);

    List<PostComment> findByPostIdAndStatus(Long postId, CommentStatus status);

    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    Page<PostComment> findApprovedCommentsByPost(@Param("postId") Long postId, Pageable pageable);

    // Parent/Child Queries
    List<PostComment> findByParentId(Long parentId);

    List<PostComment> findByParentIsNull();

    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId " +
            "AND c.parent IS NULL " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    List<PostComment> findTopLevelCommentsByPost(@Param("postId") Long postId);

    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId " +
            "AND c.parent IS NULL " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    Page<PostComment> findTopLevelCommentsByPost(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM PostComment c WHERE c.parent.id = :parentId " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt ASC")
    List<PostComment> findRepliesByParent(@Param("parentId") Long parentId);

    // Pending Comments
    @Query("SELECT c FROM PostComment c WHERE c.status = 'PENDING' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt")
    List<PostComment> findPendingComments();

    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId " +
            "AND c.status = 'PENDING' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt")
    List<PostComment> findPendingCommentsByPost(@Param("postId") Long postId);

    // Spam Detection
    @Query("SELECT c FROM PostComment c WHERE c.status = 'SPAM' " +
            "AND c.deletedAt IS NULL")
    List<PostComment> findSpamComments();

    @Query("SELECT c FROM PostComment c WHERE c.user.id = :userId " +
            "AND c.status = 'SPAM'")
    List<PostComment> findSpamCommentsByUser(@Param("userId") Long userId);

    // Recent Comments
    @Query("SELECT c FROM PostComment c WHERE c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    List<PostComment> findRecentComments(Pageable pageable);

    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    List<PostComment> findRecentCommentsByPost(@Param("postId") Long postId, Pageable pageable);

    // User Comments
    @Query("SELECT c FROM PostComment c WHERE c.user.id = :userId " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    Page<PostComment> findApprovedCommentsByUser(@Param("userId") Long userId, Pageable pageable);

    // Popular Comments (by likes)
    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.likeCount DESC, c.createdAt DESC")
    List<PostComment> findPopularCommentsByPost(@Param("postId") Long postId, Pageable pageable);

    // Search
    @Query("SELECT c FROM PostComment c WHERE " +
            "LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL")
    Page<PostComment> searchComments(@Param("keyword") String keyword, Pageable pageable);

    // Date Range
    List<PostComment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId " +
            "AND c.createdAt BETWEEN :startDate AND :endDate " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL")
    List<PostComment> findCommentsByPostAndDateRange(@Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Statistics
    @Query("SELECT COUNT(c) FROM PostComment c WHERE c.post.id = :postId " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL")
    long countApprovedCommentsByPost(@Param("postId") Long postId);

    @Query("SELECT COUNT(c) FROM PostComment c WHERE c.post.id = :postId " +
            "AND c.parent IS NULL " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL")
    long countTopLevelCommentsByPost(@Param("postId") Long postId);

    @Query("SELECT COUNT(c) FROM PostComment c WHERE c.user.id = :userId " +
            "AND c.status = 'APPROVED' " +
            "AND c.deletedAt IS NULL")
    long countApprovedCommentsByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM PostComment c WHERE c.status = :status " +
            "AND c.deletedAt IS NULL")
    long countByStatus(@Param("status") CommentStatus status);

    // Update Operations
    @Modifying
    @Query("UPDATE PostComment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :commentId")
    void incrementLikeCount(@Param("commentId") Long commentId);

    @Modifying
    @Query("UPDATE PostComment c SET c.likeCount = c.likeCount - 1 WHERE c.id = :commentId AND c.likeCount > 0")
    void decrementLikeCount(@Param("commentId") Long commentId);

    @Modifying
    @Query("UPDATE PostComment c SET c.replyCount = c.replyCount + 1 WHERE c.id = :commentId")
    void incrementReplyCount(@Param("commentId") Long commentId);

    @Modifying
    @Query("UPDATE PostComment c SET c.replyCount = c.replyCount - 1 WHERE c.id = :commentId AND c.replyCount > 0")
    void decrementReplyCount(@Param("commentId") Long commentId);

    // Bulk Operations
    @Modifying
    @Query("UPDATE PostComment c SET c.status = :newStatus WHERE c.id IN :commentIds")
    void bulkUpdateStatus(@Param("commentIds") List<Long> commentIds, @Param("newStatus") CommentStatus newStatus);

    @Modifying
    @Query("UPDATE PostComment c SET c.deletedAt = :now WHERE c.id IN :commentIds")
    void bulkSoftDelete(@Param("commentIds") List<Long> commentIds, @Param("now") LocalDateTime now);
}
