package com.badminton.entity.order;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order_shipments", indexes = {
        @Index(name = "idx_order", columnList = "order_id"),
        @Index(name = "idx_tracking", columnList = "tracking_number"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderShipment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "tracking_number", unique = true, length = 100)
    private String trackingNumber;

    @Column(name = "carrier", length = 100)
    private String carrier; // Giao Hàng Nhanh, Giao Hàng Tiết Kiệm, etc.

    @Column(name = "shipping_method", length = 50)
    private String shippingMethod; // STANDARD, EXPRESS, SAME_DAY

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.PENDING;

    // Shipping Details
    @Column(name = "shipping_address", columnDefinition = "TEXT", nullable = false)
    private String shippingAddress;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_district", length = 100)
    private String shippingDistrict;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    // Costs
    @Column(name = "shipping_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "insurance_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal insuranceFee = BigDecimal.ZERO;

    @Column(name = "cod_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal codAmount = BigDecimal.ZERO;

    // Package Info
    @Column(name = "weight", precision = 8, scale = 2)
    private BigDecimal weight; // kg

    @Column(name = "length", precision = 8, scale = 2)
    private BigDecimal length; // cm

    @Column(name = "width", precision = 8, scale = 2)
    private BigDecimal width; // cm

    @Column(name = "height", precision = 8, scale = 2)
    private BigDecimal height; // cm

    @Column(name = "package_count")
    @Builder.Default
    private Integer packageCount = 1;

    // Timestamps
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "in_transit_at")
    private LocalDateTime inTransitAt;

    @Column(name = "out_for_delivery_at")
    private LocalDateTime outForDeliveryAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    // Staff
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packed_by")
    private User packedBy;

    @Column(name = "packed_at")
    private LocalDateTime packedAt;

    @Column(name = "delivered_by_name", length = 100)
    private String deliveredByName;

    @Column(name = "delivered_by_phone", length = 20)
    private String deliveredByPhone;

    // Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "delivery_notes", columnDefinition = "TEXT")
    private String deliveryNotes;

    @Column(name = "return_reason", columnDefinition = "TEXT")
    private String returnReason;

    // Proof of Delivery
    @Column(name = "signature_image")
    private String signatureImage;

    @Column(name = "delivery_photo")
    private String deliveryPhoto;

    // Relationships
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ShipmentTracking> trackings = new HashSet<>();

    // Helper Methods

    /**
     * Ship the package
     */
    public void ship(String trackingNumber, String carrier) {
        this.status = ShipmentStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.shippedAt = LocalDateTime.now();

        addTracking("Package shipped", "Package has been picked up by carrier");
    }

    /**
     * Mark as in transit
     */
    public void markInTransit() {
        this.status = ShipmentStatus.IN_TRANSIT;
        this.inTransitAt = LocalDateTime.now();

        addTracking("In transit", "Package is on the way");
    }

    /**
     * Mark as out for delivery
     */
    public void markOutForDelivery() {
        this.status = ShipmentStatus.OUT_FOR_DELIVERY;
        this.outForDeliveryAt = LocalDateTime.now();

        addTracking("Out for delivery", "Package is out for delivery");
    }

    /**
     * Mark as delivered
     */
    public void deliver(String deliveredByName, String deliveredByPhone) {
        this.status = ShipmentStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
        this.deliveredByName = deliveredByName;
        this.deliveredByPhone = deliveredByPhone;

        addTracking("Delivered", "Package has been delivered successfully");
    }

    /**
     * Mark as returned
     */
    public void returnShipment(String reason) {
        this.status = ShipmentStatus.RETURNED;
        this.returnedAt = LocalDateTime.now();
        this.returnReason = reason;

        addTracking("Returned", "Package has been returned: " + reason);
    }

    /**
     * Add tracking entry
     */
    public void addTracking(String status, String description) {
        ShipmentTracking tracking = ShipmentTracking.builder()
                .shipment(this)
                .status(status)
                .description(description)
                .location(shippingCity)
                .build();

        trackings.add(tracking);
    }

    /**
     * Calculate volume
     */
    public BigDecimal getVolume() {
        if (length != null && width != null && height != null) {
            return length.multiply(width).multiply(height);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Check if overdue
     */
    public boolean isOverdue() {
        return expectedDeliveryDate != null &&
                LocalDateTime.now().isAfter(expectedDeliveryDate) &&
                status != ShipmentStatus.DELIVERED &&
                status != ShipmentStatus.RETURNED;
    }

    /**
     * Get delivery duration in days
     */
    public Long getDeliveryDuration() {
        if (shippedAt == null || deliveredAt == null) {
            return null;
        }

        return java.time.Duration.between(shippedAt, deliveredAt).toDays();
    }
}
