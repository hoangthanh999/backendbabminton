package com.badminton.entity.loyalty;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.booking.Booking;
import com.badminton.entity.order.Order;
import com.badminton.entity.user.User;
import com.badminton.enums.PointTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_points", indexes = {
        @Index(name = "idx_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_type", columnList = "transaction_type"),
        @Index(name = "idx_booking", columnList = "booking_id"),
        @Index(name = "idx_order", columnList = "order_id"),
        @Index(name = "idx_expiry", columnList = "expiry_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyPoint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    private PointTransactionType transactionType;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "balance_after")
    private Integer balanceAfter;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Related Transactions
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "transaction_amount", precision = 10, scale = 2)
    private BigDecimal transactionAmount;

    // Expiry
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "is_expired")
    @Builder.Default
    private Boolean isExpired = false;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    // Additional Info
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Helper Methods

    /**
     * Check if expired
     */
    public boolean checkIfExpired() {
        if (expiryDate != null && LocalDate.now().isAfter(expiryDate)) {
            this.isExpired = true;
            this.expiredAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * Get days until expiry
     */
    public Long getDaysUntilExpiry() {
        if (expiryDate == null || isExpired) {
            return null;
        }

        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    /**
     * Check if expiring soon (within 30 days)
     */
    public boolean isExpiringSoon() {
        Long days = getDaysUntilExpiry();
        return days != null && days <= 30 && days > 0;
    }
}
