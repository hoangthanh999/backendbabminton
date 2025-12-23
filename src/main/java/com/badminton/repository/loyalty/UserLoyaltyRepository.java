package com.badminton.repository.loyalty;

import com.badminton.entity.loyalty.UserLoyalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserLoyaltyRepository extends JpaRepository<UserLoyalty, Long> {

    // Basic Queries
    Optional<UserLoyalty> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<UserLoyalty> findByTierId(Long tierId);

    // Points Queries
    @Query("SELECT ul FROM UserLoyalty ul WHERE ul.availablePoints >= :minPoints " +
            "ORDER BY ul.availablePoints DESC")
    List<UserLoyalty> findByMinimumPoints(@Param("minPoints") Integer minPoints);

    @Query("SELECT ul FROM UserLoyalty ul ORDER BY ul.totalPoints DESC")
    List<UserLoyalty> findTopLoyaltyUsers(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT ul FROM UserLoyalty ul ORDER BY ul.lifetimePoints DESC")
    List<UserLoyalty> findTopLifetimeUsers(org.springframework.data.domain.Pageable pageable);

    // Tier Queries
    @Query("SELECT ul FROM UserLoyalty ul WHERE ul.tier.id = :tierId " +
            "ORDER BY ul.totalPoints DESC")
    List<UserLoyalty> findUsersByTier(@Param("tierId") Long tierId);

    @Query("SELECT ul FROM UserLoyalty ul WHERE ul.tier.level >= :minLevel " +
            "ORDER BY ul.tier.level DESC, ul.totalPoints DESC")
    List<UserLoyalty> findUsersByMinimumTierLevel(@Param("minLevel") Integer minLevel);

    // Expiring Tiers
    @Query("SELECT ul FROM UserLoyalty ul WHERE ul.tierExpiresAt IS NOT NULL " +
            "AND ul.tierExpiresAt BETWEEN :startDate AND :endDate " +
            "ORDER BY ul.tierExpiresAt")
    List<UserLoyalty> findExpiringTiers(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT ul FROM UserLoyalty ul WHERE ul.tierExpiresAt IS NOT NULL " +
            "AND ul.tierExpiresAt <= :date")
    List<UserLoyalty> findExpiredTiers(@Param("date") LocalDate date);

    // Spending Queries
    @Query("SELECT ul FROM UserLoyalty ul WHERE ul.totalSpending >= :minSpending " +
            "ORDER BY ul.totalSpending DESC")
    List<UserLoyalty> findByMinimumSpending(@Param("minSpending") BigDecimal minSpending);

    @Query("SELECT ul FROM UserLoyalty ul ORDER BY ul.totalSpending DESC")
    List<UserLoyalty> findTopSpenders(org.springframework.data.domain.Pageable pageable);

    // Membership Duration
    @Query("SELECT ul FROM UserLoyalty ul WHERE ul.memberSince <= :date " +
            "ORDER BY ul.memberSince")
    List<UserLoyalty> findLongTermMembers(@Param("date") LocalDate date);

    // Statistics
    @Query("SELECT AVG(ul.totalPoints) FROM UserLoyalty ul")
    Double getAverageTotalPoints();

    @Query("SELECT AVG(ul.availablePoints) FROM UserLoyalty ul")
    Double getAverageAvailablePoints();

    @Query("SELECT AVG(ul.totalSpending) FROM UserLoyalty ul")
    BigDecimal getAverageTotalSpending();

    @Query("SELECT SUM(ul.availablePoints) FROM UserLoyalty ul")
    Long sumTotalAvailablePoints();

    @Query("SELECT ul.tier, COUNT(ul), AVG(ul.totalPoints), AVG(ul.totalSpending) " +
            "FROM UserLoyalty ul " +
            "GROUP BY ul.tier " +
            "ORDER BY ul.tier.level")
    List<Object[]> getTierStatistics();

    // Update Operations
    @Modifying
    @Query("UPDATE UserLoyalty ul SET ul.totalPoints = ul.totalPoints + :points, " +
            "ul.availablePoints = ul.availablePoints + :points, " +
            "ul.lifetimePoints = ul.lifetimePoints + :points " +
            "WHERE ul.user.id = :userId")
    void addPoints(@Param("userId") Long userId, @Param("points") Integer points);

    @Modifying
    @Query("UPDATE UserLoyalty ul SET ul.availablePoints = ul.availablePoints - :points, " +
            "ul.redeemedPoints = ul.redeemedPoints + :points " +
            "WHERE ul.user.id = :userId AND ul.availablePoints >= :points")
    int redeemPoints(@Param("userId") Long userId, @Param("points") Integer points);

    @Modifying
    @Query("UPDATE UserLoyalty ul SET ul.totalSpending = ul.totalSpending + :amount " +
            "WHERE ul.user.id = :userId")
    void addSpending(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
