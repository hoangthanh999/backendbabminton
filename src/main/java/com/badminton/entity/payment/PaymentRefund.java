package com.badminton.entity.payment;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_refunds", indexes = {
        @Index(name = "idx_payment", columnList = "payment_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRefund extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal refundFee = BigDecimal.ZERO;

    @Column(name = "net_refund_amount", precision = 10, scale = 2)
    private BigDecimal netRefundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private RefundStatus status = RefundStatus.PENDING;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "refund_transaction_id")
    private String refundTransactionId;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    // Lifecycle
    @PrePersist
    @PreUpdate
    public void calculateNetAmount() {
        if (refundAmount != null && refundFee != null) {
            this.netRefundAmount = refundAmount.subtract(refundFee);
        }
    }

    // Helper Methods

    /**
     * Approve refund
     */
    public void approve(User approver) {
        if (status != RefundStatus.PENDING) {
            throw new IllegalStateException("Only pending refunds can be approved");
        }

        this.status = RefundStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * Process refund
     */
    public void process(String transactionId) {
        if (status != RefundStatus.APPROVED) {
            throw new IllegalStateException("Only approved refunds can be processed");
        }

        this.status = RefundStatus.COMPLETED;
        this.refundTransactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Reject refund
     */
    public void reject(User rejector, String reason) {
        if (status != RefundStatus.PENDING) {
            throw new IllegalStateException("Only pending refunds can be rejected");
        }

        this.status = RefundStatus.REJECTED;
        this.approvedBy = rejector;
        this.rejectedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    /**
     * Check if can be processed
     */
    public boolean canBeProcessed() {
        return status == RefundStatus.APPROVED;
    }
}
