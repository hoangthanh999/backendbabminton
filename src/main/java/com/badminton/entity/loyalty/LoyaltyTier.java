package com.badminton.entity.loyalty;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loyalty_tiers", uniqueConstraints = @UniqueConstraint(name = "uk_name", columnNames = "name"), indexes = {
        @Index(name = "idx_level", columnList = "level")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyTier extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name; // Bronze, Silver, Gold, Platinum, Diamond

    @Column(name = "level", nullable = false, unique = true)
    private Integer level; // 1, 2, 3, 4, 5

    @Column(name = "min_points", nullable = false)
    private Integer minPoints;

    @Column(name = "max_points")
    private Integer maxPoints;

    @Column(name = "min_spending", precision = 12, scale = 2)
    private BigDecimal minSpending;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "color", length = 7)
    private String color; // Hex color

    @Column(name = "icon")
    private String icon;

    @Column(name = "badge_image")
    private String badgeImage;

    // Benefits
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "points_multiplier", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal pointsMultiplier = new BigDecimal("1.0");

    @Column(name = "free_bookings_per_month")
    @Builder.Default
    private Integer freeBookingsPerMonth = 0;

    @Column(name = "priority_booking")
    @Builder.Default
    private Boolean priorityBooking = false;

    @Column(name = "free_equipment_rental")
    @Builder.Default
    private Boolean freeEquipmentRental = false;

    @Column(name = "birthday_bonus_points")
    @Builder.Default
    private Integer birthdayBonusPoints = 0;

    @Column(name = "benefits", columnDefinition = "JSON")
    private String benefits; // JSON array of benefits

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // Helper Methods

    /**
     * Check if points qualify for this tier
     */
    public boolean qualifiesForTier(Integer points) {
        if (points < minPoints) {
            return false;
        }

        if (maxPoints != null && points > maxPoints) {
            return false;
        }

        return true;
    }

    /**
     * Calculate points needed for this tier
     */
    public Integer getPointsNeeded(Integer currentPoints) {
        if (currentPoints >= minPoints) {
            return 0;
        }

        return minPoints - currentPoints;
    }

    /**
     * Get points range display
     */
    public String getPointsRangeDisplay() {
        if (maxPoints != null) {
            return String.format("%,d - %,d điểm", minPoints, maxPoints);
        }
        return String.format("%,d+ điểm", minPoints);
    }
}
