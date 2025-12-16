package com.badminton.entity.court;

import com.badminton.entity.base.BaseEntity;
import com.badminton.enums.DayOfWeek;
import com.badminton.enums.PriceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "court_pricing", indexes = {
        @Index(name = "idx_court_day", columnList = "court_id, day_of_week"),
        @Index(name = "idx_court_time", columnList = "court_id, time_start, time_end"),
        @Index(name = "idx_court_day_time", columnList = "court_id, day_of_week, time_start, time_end")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtPricing extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek; // 1=Monday, 7=Sunday

    @Column(name = "time_start", nullable = false)
    private LocalTime timeStart;

    @Column(name = "time_end", nullable = false)
    private LocalTime timeEnd;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    @Builder.Default
    private PriceType type = PriceType.NORMAL;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "min_booking_duration")
    @Builder.Default
    private Integer minBookingDuration = 60; // minutes

    @Column(name = "max_booking_duration")
    @Builder.Default
    private Integer maxBookingDuration = 180; // minutes

    @Column(name = "description")
    private String description;

    // Helper Methods

    /**
     * Check if time is within this pricing period
     */
    public boolean isTimeInRange(LocalTime time) {
        return !time.isBefore(timeStart) && time.isBefore(timeEnd);
    }

    /**
     * Check if pricing overlaps with another
     */
    public boolean overlapsWith(CourtPricing other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        return !(this.timeEnd.isBefore(other.timeStart) ||
                this.timeStart.isAfter(other.timeEnd));
    }

    /**
     * Get duration in hours
     */
    public double getDurationInHours() {
        long minutes = java.time.Duration.between(timeStart, timeEnd).toMinutes();
        return minutes / 60.0;
    }

    /**
     * Calculate price for duration
     */
    public BigDecimal calculatePrice(int durationMinutes) {
        if (durationMinutes < minBookingDuration) {
            throw new IllegalArgumentException("Duration below minimum: " + minBookingDuration + " minutes");
        }
        if (durationMinutes > maxBookingDuration) {
            throw new IllegalArgumentException("Duration exceeds maximum: " + maxBookingDuration + " minutes");
        }

        // Calculate hourly rate
        double hours = durationMinutes / 60.0;
        return price.multiply(BigDecimal.valueOf(hours));
    }

    /**
     * Check if this is peak hour pricing
     */
    public boolean isPeakHour() {
        return type == PriceType.PEAK;
    }

    /**
     * Check if this is discount pricing
     */
    public boolean isDiscounted() {
        return type == PriceType.DISCOUNT;
    }

    /**
     * Get price per hour
     */
    public BigDecimal getPricePerHour() {
        return price;
    }

    /**
     * Get formatted time range
     */
    public String getTimeRange() {
        return String.format("%s - %s",
                timeStart.toString(),
                timeEnd.toString());
    }

    /**
     * Get day name in Vietnamese
     */
    public String getDayNameVi() {
        return dayOfWeek.getVietnameseName();
    }
}
