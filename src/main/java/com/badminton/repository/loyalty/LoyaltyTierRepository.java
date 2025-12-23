package com.badminton.repository.loyalty;

import com.badminton.entity.loyalty.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyTierRepository extends JpaRepository<LoyaltyTier, Long> {

    // Basic Queries
    Optional<LoyaltyTier> findByName(String name);

    Optional<LoyaltyTier> findByLevel(Integer level);

    boolean existsByName(String name);

    boolean existsByLevel(Integer level);

    List<LoyaltyTier> findByIsActiveTrue();

    @Query("SELECT lt FROM LoyaltyTier lt WHERE lt.isActive = true ORDER BY lt.level")
    List<LoyaltyTier> findAllActiveTiersOrdered();

    // Points Range Queries
    @Query("SELECT lt FROM LoyaltyTier lt WHERE lt.isActive = true " +
            "AND lt.minPoints <= :points " +
            "AND (lt.maxPoints IS NULL OR lt.maxPoints >= :points) " +
            "ORDER BY lt.level DESC")
    Optional<LoyaltyTier> findTierByPoints(@Param("points") Integer points);

    @Query("SELECT lt FROM LoyaltyTier lt WHERE lt.isActive = true " +
            "AND lt.level > :currentLevel " +
            "ORDER BY lt.level")
    Optional<LoyaltyTier> findNextTier(@Param("currentLevel") Integer currentLevel);

    // Spending Range Queries
    @Query("SELECT lt FROM LoyaltyTier lt WHERE lt.isActive = true " +
            "AND lt.minSpending IS NOT NULL " +
            "AND lt.minSpending <= :spending " +
            "ORDER BY lt.level DESC")
    Optional<LoyaltyTier> findTierBySpending(@Param("spending") BigDecimal spending);

    // Statistics
    @Query("SELECT COUNT(ul) FROM UserLoyalty ul WHERE ul.tier.id = :tierId")
    long countUsersByTier(@Param("tierId") Long tierId);

    @Query("SELECT lt, COUNT(ul) FROM LoyaltyTier lt " +
            "LEFT JOIN UserLoyalty ul ON ul.tier.id = lt.id " +
            "WHERE lt.isActive = true " +
            "GROUP BY lt " +
            "ORDER BY lt.level")
    List<Object[]> getTierDistribution();
}
