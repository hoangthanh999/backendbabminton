package com.badminton.entity.payment;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.booking.Booking;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.order.Order;
import com.badminton.entity.user.User;
import com.badminton.enums.PaymentStatus;
import com.badminton.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_booking_status", columnList = "booking_id, status"),
        @Index(name = "idx_order_status", columnList = "order_id, status"),
        @Index(name = "idx_branch_created", columnList = "branch_id, created_at"),
        @Index(name = "idx_created_status", columnList = "created_at, status"),
        @Index(name = "idx_transaction", columnList = "transaction_id"),
        @Index(name = "idx_payments_complex", columnList = "booking_id, status, created_at"),
        @Index(name = "idx_user_status", columnList = "user_id, status"),
        @Index(name = "idx_method_status", columnList = "method_id, status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends AuditableEntity {

    // References
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "method_id", nullable = false)
    private PaymentMethod method;

    // Payment Details
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    @Builder.Default
    private PaymentType paymentType = PaymentType.FULL;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(name = "discount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount; // amount - fee - discount

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    // Transaction Info
    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "external_transaction_id")
    private String externalTransactionId; // From payment gateway

    @Column(name = "reference_number", unique = true)
    private String referenceNumber;

    // Gateway Info
    @Column(name = "gateway_name", length = 50)
    private String gatewayName; // VNPAY, MOMO, ZALOPAY

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse; // JSON response from gateway

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    // Timestamps
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    // Refund Info
    @Column(name = "refund_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "refund_reason", columnDefinition = "TEXT")
    private String refundReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refunded_by")
    private User refundedBy;

    // Additional Info
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    // Metadata
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "device_info", columnDefinition = "JSON")
    private String deviceInfo;

    // Retry Info
    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "max_retry")
    @Builder.Default
    private Integer maxRetry = 3;

    @Column(name = "last_retry_at")
    private LocalDateTime lastRetryAt;

    // Verification
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    // Relationships
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PaymentLog> logs = new HashSet<>();

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PaymentInstallment> installments = new HashSet<>();

    // Lifecycle Callbacks
    @PrePersist
    public void prePersist() {
        if (referenceNumber == null) {
            referenceNumber = generateReferenceNumber();
        }

        if (transactionId == null) {
            transactionId = generateTransactionId();
        }

        calculateNetAmount();

        if (customerName == null && user != null) {
            customerName = user.getName();
            customerEmail = user.getEmail();
            customerPhone = user.getPhone();
        }
    }

    @PreUpdate
    public void preUpdate() {
        calculateNetAmount();
    }

    // Helper Methods

    /**
     * Calculate net amount
     */
    public void calculateNetAmount() {
        if (amount != null) {
            BigDecimal total = amount;

            if (fee != null) {
                total = total.subtract(fee);
            }

            if (discount != null) {
                total = total.subtract(discount);
            }

            this.netAmount = total.max(BigDecimal.ZERO);
        }
    }

    /**
     * Generate reference number
     */
    private String generateReferenceNumber() {
        return String.format("PAY%s%06d",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                (int) (Math.random() * 1000000));
    }

    /**
     * Generate transaction ID
     */
    private String generateTransactionId() {
        return String.format("TXN%d%06d",
                System.currentTimeMillis(),
                (int) (Math.random() * 1000000));
    }

    /**
     * Mark as completed
     */
    public void complete() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can be completed");
        }

        this.status = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
        this.confirmedAt = LocalDateTime.now();

        addLog("Payment completed successfully", "00");
    }

    /**
     * Mark as failed
     */
    public void fail(String reason) {
        if (status == PaymentStatus.COMPLETED || status == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("Cannot fail completed or refunded payment");
        }

        this.status = PaymentStatus.FAILED;
        this.failedAt = LocalDateTime.now();

        addLog("Payment failed: " + reason, "99");
    }

    /**
     * Process refund
     */
    public void refund(BigDecimal refundAmount, String reason, User refundedBy) {
        if (status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }

        if (refundAmount.compareTo(amount) > 0) {
            throw new IllegalArgumentException("Refund amount cannot exceed payment amount");
        }

        this.status = PaymentStatus.REFUNDED;
        this.refundAmount = refundAmount;
        this.refundReason = reason;
        this.refundedBy = refundedBy;
        this.refundedAt = LocalDateTime.now();

        addLog("Payment refunded: " + refundAmount, "RF");
    }

    /**
     * Verify payment
     */
    public void verify(User verifier) {
        if (status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be verified");
        }

        this.isVerified = true;
        this.verifiedAt = LocalDateTime.now();
        this.verifiedBy = verifier;

        addLog("Payment verified", "VF");
    }

    /**
     * Retry payment
     */
    public void retry() {
        if (retryCount >= maxRetry) {
            throw new IllegalStateException("Maximum retry attempts reached");
        }

        this.retryCount++;
        this.lastRetryAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;

        addLog("Payment retry attempt " + retryCount, "RT");
    }

    /**
     * Add log entry
     */
    public void addLog(String message, String responseCode) {
        PaymentLog log = PaymentLog.builder()
                .payment(this)
                .message(message)
                .responseCode(responseCode)
                .build();

        logs.add(log);
    }

    /**
     * Check if payment is successful
     */
    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    /**
     * Check if payment is pending
     */
    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    /**
     * Check if payment is failed
     */
    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    /**
     * Check if payment is refunded
     */
    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED;
    }

    /**
     * Check if payment is expired
     */
    public boolean isExpired() {
        return expiredAt != null && LocalDateTime.now().isAfter(expiredAt);
    }

    /**
     * Check if can be retried
     */
    public boolean canRetry() {
        return status == PaymentStatus.FAILED && retryCount < maxRetry;
    }

    /**
     * Get payment source
     */
    public String getPaymentSource() {
        if (booking != null) {
            return "BOOKING";
        } else if (order != null) {
            return "ORDER";
        }
        return "OTHER";
    }

    /**
     * Get payment source ID
     */
    public Long getPaymentSourceId() {
        if (booking != null) {
            return booking.getId();
        } else if (order != null) {
            return order.getId();
        }
        return null;
    }

    /**
     * Get full payment info
     */
    public String getFullInfo() {
        return String.format("%s | %s | %s | %s",
                referenceNumber,
                method.getName(),
                amount,
                status);
    }

    /**
     * Calculate processing time in seconds
     */
    public Long getProcessingTimeInSeconds() {
        if (paidAt != null && getCreatedAt() != null) {
            return java.time.Duration.between(getCreatedAt(), paidAt).getSeconds();
        }
        return null;
    }
}
