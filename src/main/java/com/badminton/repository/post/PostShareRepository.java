package com.badminton.repository.post;

import com.badminton.entity.post.PostShare;
import com.badminton.enums.SharePlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostShareRepository extends JpaRepository<PostShare, Long> {

    // Basic Queries
    List<PostShare> findByPostId(Long postId);

    List<PostShare> findByUserId(Long userId);

    List<PostShare> findByPlatform(SharePlatform platform);

    // Count Queries
    @Query("SELECT COUNT(ps) FROM PostShare ps WHERE ps.post.id = :postId")
    long countSharesByPost(@Param("postId") Long postId);

    @Query("SELECT COUNT(ps) FROM PostShare ps WHERE ps.post.id = :postId AND ps.platform = :platform")
    long countSharesByPostAndPlatform(@Param("postId") Long postId, @Param("platform") SharePlatform platform);

    // Platform Statistics
    @Query("SELECT ps.platform, COUNT(ps) FROM PostShare ps " +
            "WHERE ps.post.id = :postId " +
            "GROUP BY ps.platform " +
            "ORDER BY COUNT(ps) DESC")
    List<Object[]> countSharesByPlatform(@Param("postId") Long postId);

    @Query("SELECT ps.platform, COUNT(ps) FROM PostShare ps " +
            "WHERE ps.createdAt >= :date " +
            "GROUP BY ps.platform " +
            "ORDER BY COUNT(ps) DESC")
    List<Object[]> getGlobalShareStatistics(@Param("date") LocalDateTime date);

    // Most Shared Posts
    @Query("SELECT ps.post.id, COUNT(ps) FROM PostShare ps " +
            "WHERE ps.createdAt >= :date " +
            "GROUP BY ps.post.id " +
            "ORDER BY COUNT(ps) DESC")
    List<Object[]> findMostSharedPosts(@Param("date") LocalDateTime date,
            org.springframework.data.domain.Pageable pageable);

    // Date Range
    List<PostShare> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT ps FROM PostShare ps WHERE ps.post.id = :postId " +
            "AND ps.createdAt BETWEEN :startDate AND :endDate")
    List<PostShare> findSharesByPostAndDateRange(@Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // User Activity
    @Query("SELECT COUNT(ps) FROM PostShare ps WHERE ps.user.id = :userId")
    long countSharesByUser(@Param("userId") Long userId);

    @Query("SELECT ps FROM PostShare ps WHERE ps.user.id = :userId " +
            "ORDER BY ps.createdAt DESC")
    List<PostShare> findRecentSharesByUser(@Param("userId") Long userId,
            org.springframework.data.domain.Pageable pageable);

    // Daily Shares
    @Query("SELECT DATE(ps.createdAt), COUNT(ps) FROM PostShare ps " +
            "WHERE ps.post.id = :postId " +
            "AND ps.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(ps.createdAt) " +
            "ORDER BY DATE(ps.createdAt)")
    List<Object[]> getDailyShareTrend(@Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
