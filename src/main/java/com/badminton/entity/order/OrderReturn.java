package com.badminton.entity.order;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.ReturnReason;
import com.badminton.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order_returns", indexes = {
        @Index(name = "idx_order", columnList = "order_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_return_number", columnList = "return_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderReturn extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "return_number", unique = true, nullable = false, length = 50)
    private String returnNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 50)
    private ReturnReason reason;

    @Column(name = "reason_detail", columnDefinition = "TEXT")
    private String reasonDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReturnStatus status = ReturnStatus.PENDING;

    @Column(name = "total_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "restocking_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal restockingFee = BigDecimal.ZERO;

    // People
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    // Timestamps
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Evidence
    @Column(name = "images", columnDefinition = "JSON")
    private String images; // JSON array of image URLs

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Relationships
    @OneToMany(mappedBy = "orderReturn", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderReturnItem> items = new HashSet<>();

    // Lifecycle
    @PrePersist
    public void prePersist() {
        if (returnNumber == null) {
            returnNumber = generateReturnNumber();
        }
    }

    // Helper Methods

    private String generateReturnNumber() {
        return String.format("RET%s%06d",
                LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                (int) (Math.random() * 1000000));
    }

    public void approve(User approver) {
        this.status = ReturnStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(User rejector, String reason) {
        this.status = ReturnStatus.REJECTED;
        this.approvedBy = rejector;
        this.rejectedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    public void complete() {
        this.status = ReturnStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void calculateRefundAmount() {
        this.refundAmount = totalAmount.subtract(restockingFee);
    }
}
