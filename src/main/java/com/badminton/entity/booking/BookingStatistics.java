package com.badminton.entity.booking;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.court.Court;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "booking_statistics", uniqueConstraints = @UniqueConstraint(name = "uk_court_date", columnNames = {
        "court_id", "date" }), indexes = {
                @Index(name = "idx_date", columnList = "date"),
                @Index(name = "idx_court", columnList = "court_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingStatistics extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "total_bookings")
    @Builder.Default
    private Integer totalBookings = 0;

    @Column(name = "confirmed_bookings")
    @Builder.Default
    private Integer confirmedBookings = 0;

    @Column(name = "completed_bookings")
    @Builder.Default
    private Integer completedBookings = 0;

    @Column(name = "cancelled_bookings")
    @Builder.Default
    private Integer cancelledBookings = 0;

    @Column(name = "no_show_bookings")
    @Builder.Default
    private Integer noShowBookings = 0;

    @Column(name = "total_revenue", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "total_hours", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal totalHours = BigDecimal.ZERO;

    @Column(name = "average_booking_value", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal averageBookingValue = BigDecimal.ZERO;

    @Column(name = "occupancy_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal occupancyRate = BigDecimal.ZERO;

    @Column(name = "peak_hour_bookings")
    @Builder.Default
    private Integer peakHourBookings = 0;

    @Column(name = "off_peak_bookings")
    @Builder.Default
    private Integer offPeakBookings = 0;

    // Helper Methods

    /**
     * Increment booking count
     */
    public void incrementBooking(Booking booking) {
        this.totalBookings++;

        switch (booking.getStatus()) {
            case CONFIRMED -> this.confirmedBookings++;
            case COMPLETED -> this.completedBookings++;
            case CANCELLED -> this.cancelledBookings++;
            case NO_SHOW -> this.noShowBookings++;
        }

        if (booking.getStatus() == com.badminton.enums.BookingStatus.COMPLETED) {
            this.totalRevenue = this.totalRevenue.add(booking.getFinalAmount());
            this.totalHours = this.totalHours.add(
                    BigDecimal.valueOf(booking.getDurationInHours()));
        }

        calculateAverages();
    }

    /**
     * Calculate averages
     */
    private void calculateAverages() {
        if (completedBookings > 0) {
            this.averageBookingValue = totalRevenue.divide(
                    BigDecimal.valueOf(completedBookings),
                    2,
                    java.math.RoundingMode.HALF_UP);
        }
    }

    /**
     * Calculate occupancy rate
     */
    public void calculateOccupancyRate(BigDecimal availableHours) {
        if (availableHours.compareTo(BigDecimal.ZERO) > 0) {
            this.occupancyRate = totalHours
                    .divide(availableHours, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }

    /**
     * Get completion rate
     */
    public BigDecimal getCompletionRate() {
        if (totalBookings == 0)
            return BigDecimal.ZERO;

        return BigDecimal.valueOf(completedBookings)
                .divide(BigDecimal.valueOf(totalBookings), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Get cancellation rate
     */
    public BigDecimal getCancellationRate() {
        if (totalBookings == 0)
            return BigDecimal.ZERO;

        return BigDecimal.valueOf(cancelledBookings)
                .divide(BigDecimal.valueOf(totalBookings), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Get no-show rate
     */
    public BigDecimal getNoShowRate() {
        if (totalBookings == 0)
            return BigDecimal.ZERO;

        return BigDecimal.valueOf(noShowBookings)
                .divide(BigDecimal.valueOf(totalBookings), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
