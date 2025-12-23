package com.badminton.repository.post;

import com.badminton.entity.post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Basic Queries
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    List<PostLike> findByPostId(Long postId);

    List<PostLike> findByUserId(Long userId);

    // Like/Dislike Queries
    List<PostLike> findByPostIdAndIsLikeTrue(Long postId);

    List<PostLike> findByPostIdAndIsLikeFalse(Long postId);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId AND pl.isLike = true")
    long countLikesByPost(@Param("postId") Long postId);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId AND pl.isLike = false")
    long countDislikesByPost(@Param("postId") Long postId);

    // User Activity
    @Query("SELECT pl FROM PostLike pl WHERE pl.user.id = :userId " +
            "AND pl.isLike = true " +
            "ORDER BY pl.createdAt DESC")
    List<PostLike> findUserLikedPosts(@Param("userId") Long userId);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.user.id = :userId AND pl.isLike = true")
    long countLikesByUser(@Param("userId") Long userId);

    // Recent Likes
    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId " +
            "AND pl.isLike = true " +
            "ORDER BY pl.createdAt DESC")
    List<PostLike> findRecentLikesByPost(@Param("postId") Long postId,
            org.springframework.data.domain.Pageable pageable);

    // Date Range
    List<PostLike> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId " +
            "AND pl.createdAt BETWEEN :startDate AND :endDate")
    List<PostLike> findLikesByPostAndDateRange(@Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Statistics
    @Query("SELECT pl.post.id, COUNT(pl) FROM PostLike pl " +
            "WHERE pl.isLike = true " +
            "AND pl.createdAt >= :date " +
            "GROUP BY pl.post.id " +
            "ORDER BY COUNT(pl) DESC")
    List<Object[]> findMostLikedPosts(@Param("date") LocalDateTime date,
            org.springframework.data.domain.Pageable pageable);

    // Delete
    void deleteByPostIdAndUserId(Long postId, Long userId);
}
