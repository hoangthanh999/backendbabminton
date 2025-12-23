package com.badminton.entity.loyalty;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "user_loyalty", uniqueConstraints = @UniqueConstraint(name = "uk_user", columnNames = "user_id"), indexes = {
        @Index(name = "idx_tier", columnList = "tier_id"),
        @Index(name = "idx_points", columnList = "total_points")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoyalty extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id")
    private LoyaltyTier tier;

    @Column(name = "total_points")
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "available_points")
    @Builder.Default
    private Integer availablePoints = 0;

    @Column(name = "redeemed_points")
    @Builder.Default
    private Integer redeemedPoints = 0;

    @Column(name = "expired_points")
    @Builder.Default
    private Integer expiredPoints = 0;

    @Column(name = "lifetime_points")
    @Builder.Default
    private Integer lifetimePoints = 0;

    @Column(name = "total_spending", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalSpending = BigDecimal.ZERO;

    @Column(name = "tier_achieved_at")
    private LocalDate tierAchievedAt;

    @Column(name = "tier_expires_at")
    private LocalDate tierExpiresAt;

    @Column(name = "next_tier_points_needed")
    private Integer nextTierPointsNeeded;

    @Column(name = "member_since")
    @Builder.Default
    private LocalDate memberSince = LocalDate.now();

    // Helper Methods

    /**
     * Add points
     */
    public void addPoints(Integer points) {
        this.totalPoints += points;
        this.availablePoints += points;
        this.lifetimePoints += points;
    }

    /**
     * Redeem points
     */
    public void redeemPoints(Integer points) {
        if (availablePoints < points) {
            throw new IllegalArgumentException("Insufficient points");
        }

        this.availablePoints -= points;
        this.redeemedPoints += points;
    }

    /**
     * Expire points
     */
    public void expirePoints(Integer points) {
        if (availablePoints < points) {
            points = availablePoints;
        }

        this.availablePoints -= points;
        this.expiredPoints += points;
    }

    /**
     * Update tier
     */
    public void updateTier(LoyaltyTier newTier) {
        this.tier = newTier;
        this.tierAchievedAt = LocalDate.now();
        // Tier expires after 1 year
        this.tierExpiresAt = LocalDate.now().plusYears(1);
    }

    /**
     * Calculate points to next tier
     */
    public Integer calculatePointsToNextTier(LoyaltyTier nextTier) {
        if (nextTier == null) {
            return null;
        }

        int needed = nextTier.getMinPoints() - totalPoints;
        this.nextTierPointsNeeded = Math.max(0, needed);
        return this.nextTierPointsNeeded;
    }

    /**
     * Get membership duration in months
     */
    public Long getMembershipDurationMonths() {
        return java.time.temporal.ChronoUnit.MONTHS.between(memberSince, LocalDate.now());
    }

    /**
     * Check if tier is expiring soon (within 30 days)
     */
    public boolean isTierExpiringSoon() {
        if (tierExpiresAt == null) {
            return false;
        }

        long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS
                .between(LocalDate.now(), tierExpiresAt);

        return daysUntilExpiry <= 30 && daysUntilExpiry > 0;
    }
}
