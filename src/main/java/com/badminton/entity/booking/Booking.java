package com.badminton.entity.booking;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.court.Court;
import com.badminton.entity.court.Schedule;
import com.badminton.entity.payment.Payment;
import com.badminton.entity.user.User;
import com.badminton.entity.feedback.Feedback;
import com.badminton.enums.BookingStatus;
import com.badminton.enums.BookingType;
import com.badminton.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_user_date_status", columnList = "user_id, date, status"),
        @Index(name = "idx_court_date_status", columnList = "court_id, date, status"),
        @Index(name = "idx_branch_date", columnList = "branch_id, date, status"),
        @Index(name = "idx_date_status", columnList = "date, status"),
        @Index(name = "idx_payment_status", columnList = "payment_status"),
        @Index(name = "idx_cancelled", columnList = "cancelled_at, cancelled_by"),
        @Index(name = "idx_created", columnList = "created_at"),
        @Index(name = "idx_bookings_complex", columnList = "court_id, date, status, time_start, time_end")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends AuditableEntity {

    // Core References
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    // Booking Details
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false, length = 20)
    @Builder.Default
    private BookingType bookingType = BookingType.SINGLE;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time_start", nullable = false)
    private LocalTime timeStart;

    @Column(name = "time_end", nullable = false)
    private LocalTime timeEnd;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    // Financial
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "deposit_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    // Additional Info
    @Column(name = "customer_name", length = 100)
    private String customerName; // For walk-in customers

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    @Column(name = "number_of_players")
    @Builder.Default
    private Integer numberOfPlayers = 2;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    // Cancellation
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by")
    private User cancelledBy;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancellation_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cancellationFee = BigDecimal.ZERO;

    // Check-in/Check-out
    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @Column(name = "checked_out_at")
    private LocalDateTime checkedOutAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_in_by")
    private User checkedInBy;

    // Recurring Booking
    @Column(name = "parent_booking_id")
    private Long parentBookingId; // For recurring bookings

    @Column(name = "recurrence_pattern")
    private String recurrencePattern; // JSON: {"frequency": "WEEKLY", "interval": 1, "endDate": "2024-12-31"}

    // Promotion
    @Column(name = "promotion_code", length = 50)
    private String promotionCode;

    @Column(name = "promotion_discount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal promotionDiscount = BigDecimal.ZERO;

    // Loyalty Points
    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0;

    @Column(name = "points_redeemed")
    @Builder.Default
    private Integer pointsRedeemed = 0;

    // Metadata
    @Column(name = "booking_source", length = 50)
    @Builder.Default
    private String bookingSource = "WEB"; // WEB, MOBILE, PHONE, WALK_IN

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    // Calculated Fields
    @Formula("(SELECT COUNT(*) FROM payments p WHERE p.booking_id = id AND p.status = 'COMPLETED')")
    private Integer paymentCount;

    @Formula("(SELECT SUM(p.amount) FROM payments p WHERE p.booking_id = id AND p.status = 'COMPLETED')")
    private BigDecimal totalPaid;

    // Relationships
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Payment> payments = new HashSet<>();

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Feedback feedback;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BookingEquipment> equipments = new HashSet<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BookingService> services = new HashSet<>();

    // Lifecycle Callbacks
    @PrePersist
    public void prePersist() {
        if (depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) == 0) {
            // Default deposit is 20% of total
            depositAmount = totalAmount.multiply(new BigDecimal("0.20"));
        }

        if (finalAmount == null || finalAmount.compareTo(BigDecimal.ZERO) == 0) {
            calculateFinalAmount();
        }

        if (customerName == null && user != null) {
            customerName = user.getName();
            customerPhone = user.getPhone();
            customerEmail = user.getEmail();
        }
    }

    @PreUpdate
    public void preUpdate() {
        calculateFinalAmount();
    }

    // Helper Methods

    /**
     * Calculate final amount after discounts
     */
    public void calculateFinalAmount() {
        BigDecimal total = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        BigDecimal promoDiscount = promotionDiscount != null ? promotionDiscount : BigDecimal.ZERO;

        this.finalAmount = total.subtract(discount).subtract(promoDiscount);

        if (this.finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.finalAmount = BigDecimal.ZERO;
        }
    }

    /**
     * Get remaining amount to pay
     */
    public BigDecimal getRemainingAmount() {
        BigDecimal paid = totalPaid != null ? totalPaid : BigDecimal.ZERO;
        return finalAmount.subtract(paid);
    }

    /**
     * Check if booking is fully paid
     */
    public boolean isFullyPaid() {
        return getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * Check if deposit is paid
     */
    public boolean isDepositPaid() {
        BigDecimal paid = totalPaid != null ? totalPaid : BigDecimal.ZERO;
        return paid.compareTo(depositAmount) >= 0;
    }

    /**
     * Get duration in minutes
     */
    public long getDurationInMinutes() {
        return java.time.Duration.between(timeStart, timeEnd).toMinutes();
    }

    /**
     * Get duration in hours
     */
    public double getDurationInHours() {
        return getDurationInMinutes() / 60.0;
    }

    /**
     * Check if booking is in the past
     */
    public boolean isPast() {
        LocalDateTime bookingDateTime = LocalDateTime.of(date, timeEnd);
        return bookingDateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Check if booking is today
     */
    public boolean isToday() {
        return date.equals(LocalDate.now());
    }

    /**
     * Check if booking is upcoming
     */
    public boolean isUpcoming() {
        LocalDateTime bookingDateTime = LocalDateTime.of(date, timeStart);
        return bookingDateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Check if can be cancelled
     */
    public boolean canBeCancelled() {
        if (status != BookingStatus.PENDING && status != BookingStatus.CONFIRMED) {
            return false;
        }

        // Can't cancel if already started or past
        LocalDateTime bookingDateTime = LocalDateTime.of(date, timeStart);
        return bookingDateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Check if can be checked in
     */
    public boolean canBeCheckedIn() {
        if (status != BookingStatus.CONFIRMED) {
            return false;
        }

        if (checkedInAt != null) {
            return false;
        }

        // Can check in 15 minutes before start time
        LocalDateTime bookingDateTime = LocalDateTime.of(date, timeStart);
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(bookingDateTime.minusMinutes(15))
                && now.isBefore(bookingDateTime.plusMinutes(30));
    }

    /**
     * Check if can be checked out
     */
    public boolean canBeCheckedOut() {
        return checkedInAt != null && checkedOutAt == null;
    }

    /**
     * Cancel booking
     */
    public void cancel(User cancelledBy, String reason, BigDecimal fee) {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Booking cannot be cancelled");
        }

        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;
        this.cancellationFee = fee != null ? fee : BigDecimal.ZERO;
    }

    /**
     * Confirm booking
     */
    public void confirm() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        this.status = BookingStatus.CONFIRMED;
    }

    /**
     * Check in
     */
    public void checkIn(User staff) {
        if (!canBeCheckedIn()) {
            throw new IllegalStateException("Booking cannot be checked in");
        }

        this.checkedInAt = LocalDateTime.now();
        this.checkedInBy = staff;
    }

    /**
     * Check out
     */
    public void checkOut() {
        if (!canBeCheckedOut()) {
            throw new IllegalStateException("Booking cannot be checked out");
        }

        this.checkedOutAt = LocalDateTime.now();
        this.status = BookingStatus.COMPLETED;
    }

    /**
     * Mark as no show
     */
    public void markAsNoShow() {
        if (status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be marked as no show");
        }

        if (!isPast()) {
            throw new IllegalStateException("Cannot mark future booking as no show");
        }

        this.status = BookingStatus.NO_SHOW;
    }

    /**
     * Add payment
     */
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setBooking(this);
        updatePaymentStatus();
    }

    /**
     * Update payment status based on total paid
     */
    public void updatePaymentStatus() {
        BigDecimal paid = totalPaid != null ? totalPaid : BigDecimal.ZERO;

        if (paid.compareTo(BigDecimal.ZERO) == 0) {
            this.paymentStatus = PaymentStatus.UNPAID;
        } else if (paid.compareTo(finalAmount) >= 0) {
            this.paymentStatus = PaymentStatus.PAID;
        } else if (paid.compareTo(depositAmount) >= 0) {
            this.paymentStatus = PaymentStatus.DEPOSIT;
        } else {
            this.paymentStatus = PaymentStatus.PARTIAL;
        }
    }

    /**
     * Apply promotion
     */
    public void applyPromotion(String code, BigDecimal discount) {
        this.promotionCode = code;
        this.promotionDiscount = discount;
        calculateFinalAmount();
    }

    /**
     * Add equipment rental
     */
    public void addEquipment(BookingEquipment equipment) {
        equipments.add(equipment);
        equipment.setBooking(this);

        // Update total amount
        this.totalAmount = this.totalAmount.add(equipment.getTotalPrice());
        calculateFinalAmount();
    }

    /**
     * Add service
     */
    public void addService(BookingService service) {
        services.add(service);
        service.setBooking(this);

        // Update total amount
        this.totalAmount = this.totalAmount.add(service.getTotalPrice());
        calculateFinalAmount();
    }

    /**
     * Get booking reference number
     */
    public String getReferenceNumber() {
        return String.format("BK%s%06d",
                date.toString().replace("-", ""),
                getId() != null ? getId() : 0);
    }

    /**
     * Get time range string
     */
    public String getTimeRange() {
        return String.format("%s - %s", timeStart, timeEnd);
    }

    /**
     * Get full booking info
     */
    public String getFullInfo() {
        return String.format("%s | %s | %s | %s",
                getReferenceNumber(),
                court.getName(),
                date,
                getTimeRange());
    }
}
