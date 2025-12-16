package com.badminton.entity.user;

import com.badminton.entity.base.BaseEntity;
import com.badminton.enums.LoyaltyTier;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_loyalty_summary", uniqueConstraints = @UniqueConstraint(name = "uk_user", columnNames = "user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoyaltySummary extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "available_points", nullable = false)
    @Builder.Default
    private Integer availablePoints = 0;

    @Column(name = "lifetime_points", nullable = false)
    @Builder.Default
    private Integer lifetimePoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", length = 20)
    @Builder.Default
    private LoyaltyTier tier = LoyaltyTier.BRONZE;

    @Column(name = "tier_updated_at")
    private LocalDateTime tierUpdatedAt;

    // Helper methods
    public void addPoints(Integer points) {
        this.totalPoints += points;
        this.availablePoints += points;
        this.lifetimePoints += points;
        updateTier();
    }

    public void redeemPoints(Integer points) {
        if (this.availablePoints >= points) {
            this.availablePoints -= points;
        } else {
            throw new IllegalArgumentException("Insufficient points");
        }
    }

    private void updateTier() {
        LoyaltyTier newTier;
        if (lifetimePoints >= 1000) {
            newTier = LoyaltyTier.PLATINUM;
        } else if (lifetimePoints >= 500) {
            newTier = LoyaltyTier.GOLD;
        } else if (lifetimePoints >= 200) {
            newTier = LoyaltyTier.SILVER;
        } else {
            newTier = LoyaltyTier.BRONZE;
        }

        if (this.tier != newTier) {
            this.tier = newTier;
            this.tierUpdatedAt = LocalDateTime.now();
        }
    }
}
