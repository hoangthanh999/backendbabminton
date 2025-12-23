package com.badminton.repository.loyalty;

import com.badminton.entity.loyalty.LoyaltyPoint;
import com.badminton.enums.PointTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Long> {

    // Basic Queries
    List<LoyaltyPoint> findByUserId(Long userId);

    Page<LoyaltyPoint> findByUserId(Long userId, Pageable pageable);

    List<LoyaltyPoint> findByBookingId(Long bookingId);

    List<LoyaltyPoint> findByOrderId(Long orderId);

    // Transaction Type Queries
    List<LoyaltyPoint> findByTransactionType(PointTransactionType transactionType);

    List<LoyaltyPoint> findByUserIdAndTransactionType(Long userId, PointTransactionType transactionType);

    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.transactionType IN :types " +
            "ORDER BY lp.createdAt DESC")
    List<LoyaltyPoint> findByUserAndTransactionTypes(@Param("userId") Long userId,
            @Param("types") List<PointTransactionType> types);

    // Earning Queries
    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.points > 0 " +
            "ORDER BY lp.createdAt DESC")
    List<LoyaltyPoint> findEarningTransactions(@Param("userId") Long userId);

    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.points > 0 " +
            "ORDER BY lp.createdAt DESC")
    Page<LoyaltyPoint> findEarningTransactions(@Param("userId") Long userId, Pageable pageable);

    // Redemption Queries
    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.points < 0 " +
            "ORDER BY lp.createdAt DESC")
    List<LoyaltyPoint> findRedemptionTransactions(@Param("userId") Long userId);

    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.points < 0 " +
            "ORDER BY lp.createdAt DESC")
    Page<LoyaltyPoint> findRedemptionTransactions(@Param("userId") Long userId, Pageable pageable);

    // Expiry Queries
    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.expiryDate IS NOT NULL " +
            "AND lp.expiryDate <= :date " +
            "AND lp.isExpired = false " +
            "AND lp.points > 0")
    List<LoyaltyPoint> findExpiredPoints(@Param("date") LocalDate date);

    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.expiryDate IS NOT NULL " +
            "AND lp.expiryDate BETWEEN :startDate AND :endDate " +
            "AND lp.isExpired = false " +
            "AND lp.points > 0 " +
            "ORDER BY lp.expiryDate")
    List<LoyaltyPoint> findExpiringPoints(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.expiryDate IS NOT NULL " +
            "AND lp.expiryDate > :date " +
            "AND lp.isExpired = false " +
            "AND lp.points > 0 " +
            "ORDER BY lp.expiryDate")
    List<LoyaltyPoint> findActivePointsWithExpiry(@Param("userId") Long userId,
            @Param("date") LocalDate date);

    // Date Range Queries
    List<LoyaltyPoint> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY lp.createdAt DESC")
    List<LoyaltyPoint> findByUserAndDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Statistics
    @Query("SELECT SUM(lp.points) FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.points > 0 " +
            "AND lp.isExpired = false")
    Integer sumEarnedPointsByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(lp.points) FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.points < 0")
    Integer sumRedeemedPointsByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(lp.points) FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.isExpired = true")
    Integer sumExpiredPointsByUser(@Param("userId") Long userId);

    @Query("SELECT lp.transactionType, SUM(lp.points) FROM LoyaltyPoint lp " +
            "WHERE lp.user.id = :userId " +
            "GROUP BY lp.transactionType")
    List<Object[]> getPointsByTransactionType(@Param("userId") Long userId);

    @Query("SELECT COUNT(lp) FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "AND lp.transactionType = :type")
    long countTransactionsByType(@Param("userId") Long userId,
            @Param("type") PointTransactionType type);

    // Recent Transactions
    @Query("SELECT lp FROM LoyaltyPoint lp WHERE lp.user.id = :userId " +
            "ORDER BY lp.createdAt DESC")
    List<LoyaltyPoint> findRecentTransactions(@Param("userId") Long userId, Pageable pageable);

    // Monthly Summary
    @Query("SELECT YEAR(lp.createdAt), MONTH(lp.createdAt), SUM(lp.points) " +
            "FROM LoyaltyPoint lp " +
            "WHERE lp.user.id = :userId " +
            "AND lp.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(lp.createdAt), MONTH(lp.createdAt) " +
            "ORDER BY YEAR(lp.createdAt), MONTH(lp.createdAt)")
    List<Object[]> getMonthlyPointsSummary(@Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
