package com.badminton.entity.inventory;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.product.Product;
import com.badminton.entity.user.User;
import com.badminton.enums.TransferStatus;
import com.badminton.enums.TransferType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transfers", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_branches", columnList = "from_branch_id, to_branch_id"),
        @Index(name = "idx_product", columnList = "product_id"),
        @Index(name = "idx_request_date", columnList = "request_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransfer extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch_id")
    private Branch fromBranch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_branch_id", nullable = false)
    private Branch toBranch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", length = 30)
    @Builder.Default
    private TransferType transferType = TransferType.BRANCH_TO_BRANCH;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TransferStatus status = TransferStatus.PENDING;

    // People involved
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;

    // Timestamps
    @Column(name = "request_date", nullable = false)
    @Builder.Default
    private LocalDateTime requestDate = LocalDateTime.now();

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    // Additional Info
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "shipping_method", length = 100)
    private String shippingMethod;

    @Column(name = "received_quantity")
    private Integer receivedQuantity;

    @Column(name = "damaged_quantity")
    @Builder.Default
    private Integer damagedQuantity = 0;

    // Helper Methods

    /**
     * Approve transfer
     */
    public void approve(User approver) {
        if (status != TransferStatus.PENDING) {
            throw new IllegalStateException("Only pending transfers can be approved");
        }

        this.status = TransferStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedDate = LocalDateTime.now();
    }

    /**
     * Reject transfer
     */
    public void reject(User rejector, String reason) {
        if (status != TransferStatus.PENDING) {
            throw new IllegalStateException("Only pending transfers can be rejected");
        }

        this.status = TransferStatus.REJECTED;
        this.approvedBy = rejector;
        this.approvedDate = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    /**
     * Ship transfer
     */
    public void ship(String trackingNumber, String shippingMethod) {
        if (status != TransferStatus.APPROVED) {
            throw new IllegalStateException("Only approved transfers can be shipped");
        }

        this.status = TransferStatus.IN_TRANSIT;
        this.shippedDate = LocalDateTime.now();
        this.trackingNumber = trackingNumber;
        this.shippingMethod = shippingMethod;
    }

    /**
     * Complete transfer
     */
    public void complete(User receiver, Integer receivedQty, Integer damagedQty) {
        if (status != TransferStatus.IN_TRANSIT) {
            throw new IllegalStateException("Only in-transit transfers can be completed");
        }

        this.status = TransferStatus.COMPLETED;
        this.receivedBy = receiver;
        this.receivedDate = LocalDateTime.now();
        this.receivedQuantity = receivedQty;
        this.damagedQuantity = damagedQty != null ? damagedQty : 0;
    }

    /**
     * Cancel transfer
     */
    public void cancel(String reason) {
        if (status == TransferStatus.COMPLETED) {
            throw new IllegalStateException("Completed transfers cannot be cancelled");
        }

        this.status = TransferStatus.CANCELLED;
        this.rejectionReason = reason;
    }

    /**
     * Check if overdue
     */
    public boolean isOverdue() {
        return status == TransferStatus.IN_TRANSIT &&
                expectedDeliveryDate != null &&
                LocalDateTime.now().isAfter(expectedDeliveryDate);
    }

    /**
     * Get transit days
     */
    public Long getTransitDays() {
        if (shippedDate == null)
            return null;

        LocalDateTime endDate = receivedDate != null ? receivedDate : LocalDateTime.now();
        return java.time.Duration.between(shippedDate, endDate).toDays();
    }

    /**
     * Get transfer reference
     */
    public String getReferenceNumber() {
        return String.format("TRF%s%06d",
                requestDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                getId() != null ? getId() : 0);
    }
}
