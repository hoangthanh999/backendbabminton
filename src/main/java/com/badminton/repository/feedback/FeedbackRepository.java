package com.badminton.repository.feedback;

import com.badminton.entity.feedback.Feedback;
import com.badminton.enums.FeedbackCategory;
import com.badminton.enums.FeedbackStatus;
import com.badminton.enums.FeedbackType;
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

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {

    // Basic Queries
    List<Feedback> findByBranchId(Long branchId);

    Page<Feedback> findByBranchId(Long branchId, Pageable pageable);

    List<Feedback> findByUserId(Long userId);

    Page<Feedback> findByUserId(Long userId, Pageable pageable);

    List<Feedback> findByBookingId(Long bookingId);

    List<Feedback> findByOrderId(Long orderId);

    // Status Queries
    List<Feedback> findByStatus(FeedbackStatus status);

    Page<Feedback> findByStatus(FeedbackStatus status, Pageable pageable);

    List<Feedback> findByBranchIdAndStatus(Long branchId, FeedbackStatus status);

    Page<Feedback> findByBranchIdAndStatus(Long branchId, FeedbackStatus status, Pageable pageable);

    // Type & Category Queries
    List<Feedback> findByFeedbackType(FeedbackType feedbackType);

    List<Feedback> findByCategory(FeedbackCategory category);

    List<Feedback> findByBranchIdAndFeedbackType(Long branchId, FeedbackType feedbackType);

    List<Feedback> findByBranchIdAndCategory(Long branchId, FeedbackCategory category);

    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.feedbackType = :type " +
            "AND f.category = :category")
    List<Feedback> findByBranchTypeAndCategory(@Param("branchId") Long branchId,
            @Param("type") FeedbackType type,
            @Param("category") FeedbackCategory category);

    // Rating Queries
    List<Feedback> findByRating(Integer rating);

    List<Feedback> findByBranchIdAndRating(Long branchId, Integer rating);

    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.rating >= :minRating " +
            "ORDER BY f.rating DESC")
    List<Feedback> findByBranchAndMinRating(@Param("branchId") Long branchId,
            @Param("minRating") Integer minRating);

    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.rating <= :maxRating " +
            "ORDER BY f.rating ASC")
    List<Feedback> findByBranchAndMaxRating(@Param("branchId") Long branchId,
            @Param("maxRating") Integer maxRating);

    // Pending Feedback
    @Query("SELECT f FROM Feedback f WHERE f.status = 'PENDING' " +
            "ORDER BY f.createdAt")
    List<Feedback> findPendingFeedback();

    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.status = 'PENDING' " +
            "ORDER BY f.createdAt")
    List<Feedback> findPendingFeedbackByBranch(@Param("branchId") Long branchId);

    // Published Feedback
    @Query("SELECT f FROM Feedback f WHERE f.isPublished = true " +
            "AND f.status = 'APPROVED' " +
            "ORDER BY f.publishedAt DESC")
    Page<Feedback> findPublishedFeedback(Pageable pageable);

    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.isPublished = true " +
            "AND f.status = 'APPROVED' " +
            "ORDER BY f.publishedAt DESC")
    Page<Feedback> findPublishedFeedbackByBranch(@Param("branchId") Long branchId, Pageable pageable);

    // Verified Customer
    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.isVerifiedCustomer = true " +
            "AND f.isPublished = true " +
            "ORDER BY f.createdAt DESC")
    Page<Feedback> findVerifiedFeedback(@Param("branchId") Long branchId, Pageable pageable);

    // Response Status
    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.status = 'APPROVED' " +
            "AND f.response IS NULL " +
            "ORDER BY f.createdAt")
    List<Feedback> findFeedbackNeedingResponse(@Param("branchId") Long branchId);

    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.response IS NOT NULL " +
            "ORDER BY f.respondedAt DESC")
    List<Feedback> findRespondedFeedback(@Param("branchId") Long branchId);

    List<Feedback> findByRespondedById(Long userId);

    // Search Queries
    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND (LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(f.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Feedback> searchFeedback(@Param("branchId") Long branchId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Date Range Queries
    List<Feedback> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY f.createdAt DESC")
    List<Feedback> findByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Statistics Queries
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.status = :status")
    long countByBranchAndStatus(@Param("branchId") Long branchId,
            @Param("status") FeedbackStatus status);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.rating = :rating")
    long countByBranchAndRating(@Param("branchId") Long branchId,
            @Param("rating") Integer rating);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.status = 'APPROVED'")
    Double getAverageRatingByBranch(@Param("branchId") Long branchId);

    @Query("SELECT f.rating, COUNT(f) FROM Feedback f " +
            "WHERE f.branch.id = :branchId " +
            "AND f.status = 'APPROVED' " +
            "GROUP BY f.rating " +
            "ORDER BY f.rating DESC")
    List<Object[]> getRatingDistribution(@Param("branchId") Long branchId);

    @Query("SELECT f.category, COUNT(f) FROM Feedback f " +
            "WHERE f.branch.id = :branchId " +
            "GROUP BY f.category " +
            "ORDER BY COUNT(f) DESC")
    List<Object[]> getCategoryStatistics(@Param("branchId") Long branchId);

    @Query("SELECT f.feedbackType, COUNT(f) FROM Feedback f " +
            "WHERE f.branch.id = :branchId " +
            "GROUP BY f.feedbackType " +
            "ORDER BY COUNT(f) DESC")
    List<Object[]> getTypeStatistics(@Param("branchId") Long branchId);

    // Detailed Ratings
    @Query("SELECT AVG(f.serviceRating) FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.serviceRating IS NOT NULL")
    Double getAverageServiceRating(@Param("branchId") Long branchId);

    @Query("SELECT AVG(f.facilityRating) FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.facilityRating IS NOT NULL")
    Double getAverageFacilityRating(@Param("branchId") Long branchId);

    @Query("SELECT AVG(f.cleanlinessRating) FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.cleanlinessRating IS NOT NULL")
    Double getAverageCleanlinessRating(@Param("branchId") Long branchId);

    @Query("SELECT AVG(f.valueRating) FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.valueRating IS NOT NULL")
    Double getAverageValueRating(@Param("branchId") Long branchId);

    @Query("SELECT AVG(f.staffRating) FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.staffRating IS NOT NULL")
    Double getAverageStaffRating(@Param("branchId") Long branchId);

    // Helpful Feedback
    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.isPublished = true " +
            "ORDER BY f.helpfulCount DESC")
    List<Feedback> findMostHelpfulFeedback(@Param("branchId") Long branchId, Pageable pageable);

    // Recent Feedback
    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.isPublished = true " +
            "ORDER BY f.createdAt DESC")
    List<Feedback> findRecentFeedback(@Param("branchId") Long branchId, Pageable pageable);

    // High/Low Ratings
    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.rating >= 4 " +
            "AND f.isPublished = true " +
            "ORDER BY f.rating DESC, f.createdAt DESC")
    List<Feedback> findPositiveFeedback(@Param("branchId") Long branchId, Pageable pageable);

    @Query("SELECT f FROM Feedback f WHERE f.branch.id = :branchId " +
            "AND f.rating <= 2 " +
            "AND f.status = 'APPROVED' " +
            "ORDER BY f.rating ASC, f.createdAt DESC")
    List<Feedback> findNegativeFeedback(@Param("branchId") Long branchId, Pageable pageable);

    // Update Operations
    @Modifying
    @Query("UPDATE Feedback f SET f.helpfulCount = f.helpfulCount + 1 WHERE f.id = :feedbackId")
    void incrementHelpfulCount(@Param("feedbackId") Long feedbackId);

    @Modifying
    @Query("UPDATE Feedback f SET f.notHelpfulCount = f.notHelpfulCount + 1 WHERE f.id = :feedbackId")
    void incrementNotHelpfulCount(@Param("feedbackId") Long feedbackId);

    // Monthly Trend
    @Query("SELECT YEAR(f.createdAt), MONTH(f.createdAt), COUNT(f), AVG(f.rating) " +
            "FROM Feedback f " +
            "WHERE f.branch.id = :branchId " +
            "AND f.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(f.createdAt), MONTH(f.createdAt) " +
            "ORDER BY YEAR(f.createdAt), MONTH(f.createdAt)")
    List<Object[]> getFeedbackTrend(@Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
