package com.badminton.entity.order;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.product.Product;
import com.badminton.entity.product.Service;
import com.badminton.enums.ItemType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "idx_order", columnList = "order_id"),
        @Index(name = "idx_item", columnList = "item_id, item_type"),
        @Index(name = "idx_product", columnList = "product_id"),
        @Index(name = "idx_service", columnList = "service_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Polymorphic reference (item can be Product or Service)
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private ItemType itemType; // PRODUCT or SERVICE

    // Direct references for easier querying
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    // Item Details (snapshot at time of order)
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_sku", length = 50)
    private String itemSku;

    @Column(name = "item_description", columnDefinition = "TEXT")
    private String itemDescription;

    @Column(name = "item_image")
    private String itemImage;

    // Pricing
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    // Product Attributes (for variants)
    @Column(name = "attributes", columnDefinition = "JSON")
    private String attributes; // {"color": "red", "size": "XL"}

    // Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "customization", columnDefinition = "TEXT")
    private String customization;

    // Status
    @Column(name = "is_gift")
    @Builder.Default
    private Boolean isGift = false;

    @Column(name = "gift_message")
    private String giftMessage;

    // Return/Refund
    @Column(name = "is_returnable")
    @Builder.Default
    private Boolean isReturnable = true;

    @Column(name = "returned_quantity")
    @Builder.Default
    private Integer returnedQuantity = 0;

    @Column(name = "refunded_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    // Lifecycle Callbacks
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            // Calculate base total
            BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

            // Subtract discount
            if (discountAmount != null) {
                total = total.subtract(discountAmount);
            }

            // Add tax
            if (taxAmount != null) {
                total = total.add(taxAmount);
            }

            this.totalPrice = total.max(BigDecimal.ZERO);
        }

        // Set references based on item type
        if (itemType == ItemType.PRODUCT && product != null) {
            this.itemId = product.getId();
            if (itemName == null) {
                this.itemName = product.getName();
                this.itemSku = product.getSku();
                this.itemDescription = product.getShortDescription();
                this.itemImage = product.getMainImage();
            }
        } else if (itemType == ItemType.SERVICE && service != null) {
            this.itemId = service.getId();
            if (itemName == null) {
                this.itemName = service.getName();
                this.itemDescription = service.getDescription();
                this.itemImage = service.getImage();
            }
        }
    }

    // Helper Methods

    /**
     * Calculate discount amount from percentage
     */
    public void applyDiscountPercentage(BigDecimal percentage) {
        this.discountPercentage = percentage;

        BigDecimal baseAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.discountAmount = baseAmount
                .multiply(percentage)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

        calculateTotalPrice();
    }

    /**
     * Calculate tax
     */
    public void calculateTax(BigDecimal rate) {
        this.taxRate = rate;

        BigDecimal taxableAmount = unitPrice
                .multiply(BigDecimal.valueOf(quantity))
                .subtract(discountAmount);

        this.taxAmount = taxableAmount
                .multiply(rate)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

        calculateTotalPrice();
    }

    /**
     * Get subtotal (before tax)
     */
    public BigDecimal getSubtotal() {
        return unitPrice
                .multiply(BigDecimal.valueOf(quantity))
                .subtract(discountAmount);
    }

    /**
     * Get discount per unit
     */
    public BigDecimal getDiscountPerUnit() {
        if (quantity == 0)
            return BigDecimal.ZERO;

        return discountAmount.divide(
                BigDecimal.valueOf(quantity),
                2,
                java.math.RoundingMode.HALF_UP);
    }

    /**
     * Get final price per unit
     */
    public BigDecimal getFinalPricePerUnit() {
        return unitPrice.subtract(getDiscountPerUnit());
    }

    /**
     * Check if has discount
     */
    public boolean hasDiscount() {
        return discountAmount != null &&
                discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if can be returned
     */
    public boolean canBeReturned() {
        return isReturnable &&
                returnedQuantity < quantity;
    }

    /**
     * Get remaining returnable quantity
     */
    public Integer getReturnableQuantity() {
        return quantity - returnedQuantity;
    }

    /**
     * Process return
     */
    public void processReturn(Integer returnQty, BigDecimal refundAmt) {
        if (returnQty > getReturnableQuantity()) {
            throw new IllegalArgumentException("Return quantity exceeds available quantity");
        }

        this.returnedQuantity += returnQty;
        this.refundedAmount = this.refundedAmount.add(refundAmt);
    }

    /**
     * Get item display name with attributes
     */
    public String getDisplayName() {
        if (attributes != null && !attributes.isEmpty()) {
            return itemName + " (" + attributes + ")";
        }
        return itemName;
    }
}
