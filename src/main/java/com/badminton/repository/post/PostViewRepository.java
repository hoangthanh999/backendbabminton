package com.badminton.repository.post;

import com.badminton.entity.post.PostView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, Long> {

    // Basic Queries
    List<PostView> findByPostId(Long postId);

    List<PostView> findByUserId(Long userId);

    Optional<PostView> findByPostIdAndSessionId(Long postId, String sessionId);

    // Unique Views
    @Query("SELECT COUNT(DISTINCT pv.sessionId) FROM PostView pv WHERE pv.post.id = :postId")
    long countUniqueViewsByPost(@Param("postId") Long postId);

    @Query("SELECT COUNT(DISTINCT pv.ipAddress) FROM PostView pv WHERE pv.post.id = :postId")
    long countUniqueIpViewsByPost(@Param("postId") Long postId);

    @Query("SELECT COUNT(DISTINCT pv.user.id) FROM PostView pv WHERE pv.post.id = :postId AND pv.user IS NOT NULL")
    long countUniqueUserViewsByPost(@Param("postId") Long postId);

    // Date Range
    List<PostView> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT pv FROM PostView pv WHERE pv.post.id = :postId " +
            "AND pv.createdAt BETWEEN :startDate AND :endDate")
    List<PostView> findViewsByPostAndDateRange(@Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Device Statistics
    @Query("SELECT pv.deviceType, COUNT(pv) FROM PostView pv " +
            "WHERE pv.post.id = :postId " +
            "GROUP BY pv.deviceType")
    List<Object[]> countViewsByDeviceType(@Param("postId") Long postId);

    @Query("SELECT pv.browser, COUNT(pv) FROM PostView pv " +
            "WHERE pv.post.id = :postId " +
            "GROUP BY pv.browser")
    List<Object[]> countViewsByBrowser(@Param("postId") Long postId);

    @Query("SELECT pv.os, COUNT(pv) FROM PostView pv " +
            "WHERE pv.post.id = :postId " +
            "GROUP BY pv.os")
    List<Object[]> countViewsByOS(@Param("postId") Long postId);

    // Geographic Statistics
    @Query("SELECT pv.country, COUNT(pv) FROM PostView pv " +
            "WHERE pv.post.id = :postId " +
            "AND pv.country IS NOT NULL " +
            "GROUP BY pv.country " +
            "ORDER BY COUNT(pv) DESC")
    List<Object[]> countViewsByCountry(@Param("postId") Long postId);

    @Query("SELECT pv.city, COUNT(pv) FROM PostView pv " +
            "WHERE pv.post.id = :postId " +
            "AND pv.city IS NOT NULL " +
            "GROUP BY pv.city " +
            "ORDER BY COUNT(pv) DESC")
    List<Object[]> countViewsByCity(@Param("postId") Long postId);

    // Referrer Analysis
    @Query("SELECT pv.referrer, COUNT(pv) FROM PostView pv " +
            "WHERE pv.post.id = :postId " +
            "AND pv.referrer IS NOT NULL " +
            "GROUP BY pv.referrer " +
            "ORDER BY COUNT(pv) DESC")
    List<Object[]> countViewsByReferrer(@Param("postId") Long postId);

    // Engagement Metrics
    @Query("SELECT AVG(pv.timeSpentSeconds) FROM PostView pv WHERE pv.post.id = :postId")
    Double getAverageTimeSpent(@Param("postId") Long postId);

    @Query("SELECT AVG(pv.scrollDepthPercentage) FROM PostView pv WHERE pv.post.id = :postId")
    Double getAverageScrollDepth(@Param("postId") Long postId);

    // Trending Analysis
    @Query("SELECT pv.post.id, COUNT(pv) FROM PostView pv " +
            "WHERE pv.createdAt >= :date " +
            "GROUP BY pv.post.id " +
            "ORDER BY COUNT(pv) DESC")
    List<Object[]> findTrendingPosts(@Param("date") LocalDateTime date,
            org.springframework.data.domain.Pageable pageable);

    // Hourly Distribution
    @Query("SELECT HOUR(pv.createdAt), COUNT(pv) FROM PostView pv " +
            "WHERE pv.post.id = :postId " +
            "GROUP BY HOUR(pv.createdAt) " +
            "ORDER BY HOUR(pv.createdAt)")
    List<Object[]> getHourlyViewDistribution(@Param("postId") Long postId);

    // Daily Views
    @Query("SELECT DATE(pv.createdAt), COUNT(pv) FROM PostView pv " +
            "WHERE pv.post.id = :postId " +
            "AND pv.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(pv.createdAt) " +
            "ORDER BY DATE(pv.createdAt)")
    List<Object[]> getDailyViewTrend(@Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
