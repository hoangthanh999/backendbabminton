package com.badminton.entity.order;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.payment.Payment;
import com.badminton.entity.user.User;
import com.badminton.enums.OrderStatus;
import com.badminton.enums.OrderType;
import com.badminton.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_branch_status", columnList = "branch_id, status"),
        @Index(name = "idx_status_created", columnList = "status, created_at"),
        @Index(name = "idx_orders_complex", columnList = "user_id, status, created_at"),
        @Index(name = "idx_order_number", columnList = "order_number"),
        @Index(name = "idx_payment_status", columnList = "payment_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AuditableEntity {

    // Core References
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Order Info
    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 20)
    @Builder.Default
    private OrderType orderType = OrderType.RETAIL;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    // Customer Info
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    // Shipping Info
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_district", length = 100)
    private String shippingDistrict;

    @Column(name = "shipping_ward", length = 100)
    private String shippingWard;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Column(name = "shipping_method", length = 50)
    private String shippingMethod; // PICKUP, DELIVERY, EXPRESS

    @Column(name = "shipping_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    // Financial
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

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

    @Column(name = "points_value", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal pointsValue = BigDecimal.ZERO;

    // Timestamps
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "processing_at")
    private LocalDateTime processingAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    // Cancellation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by")
    private User cancelledBy;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // Additional Info
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @Column(name = "gift_message", columnDefinition = "TEXT")
    private String giftMessage;

    @Column(name = "is_gift")
    @Builder.Default
    private Boolean isGift = false;

    // Source
    @Column(name = "order_source", length = 50)
    @Builder.Default
    private String orderSource = "WEB"; // WEB, MOBILE, POS, PHONE

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    // Staff Assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    // Invoice
    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    @Column(name = "tax_invoice_required")
    @Builder.Default
    private Boolean taxInvoiceRequired = false;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_tax_code", length = 50)
    private String companyTaxCode;

    @Column(name = "company_address", columnDefinition = "TEXT")
    private String companyAddress;

    // Calculated Fields
    @Formula("(SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = id)")
    private Integer itemCount;

    @Formula("(SELECT SUM(oi.quantity) FROM order_items oi WHERE oi.order_id = id)")
    private Integer totalQuantity;

    @Formula("(SELECT COUNT(*) FROM payments p WHERE p.order_id = id AND p.status = 'COMPLETED')")
    private Integer paymentCount;

    @Formula("(SELECT SUM(p.amount) FROM payments p WHERE p.order_id = id AND p.status = 'COMPLETED')")
    private BigDecimal totalPaid;

    // Relationships
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderItem> items = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Payment> payments = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderHistory> histories = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderShipment> shipments = new HashSet<>();

    // Lifecycle Callbacks
    @PrePersist
    public void prePersist() {
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }

        if (customerName == null && user != null) {
            customerName = user.getName();
            customerEmail = user.getEmail();
            customerPhone = user.getPhone();
        }

        calculateTotals();
    }

    @PreUpdate
    public void preUpdate() {
        calculateTotals();
    }

    // Helper Methods

    /**
     * Generate order number
     */
    private String generateOrderNumber() {
        return String.format("ORD%s%06d",
                LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                (int) (Math.random() * 1000000));
    }

    /**
     * Calculate totals
     */
    public void calculateTotals() {
        // Calculate subtotal from items
        this.subtotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total
        BigDecimal total = subtotal;

        // Add shipping fee
        if (shippingFee != null) {
            total = total.add(shippingFee);
        }

        // Add tax
        if (taxAmount != null) {
            total = total.add(taxAmount);
        }

        // Subtract discounts
        if (discountAmount != null) {
            total = total.subtract(discountAmount);
        }

        if (promotionDiscount != null) {
            total = total.subtract(promotionDiscount);
        }

        if (pointsValue != null) {
            total = total.subtract(pointsValue);
        }

        this.totalAmount = total.max(BigDecimal.ZERO);
    }

    /**
     * Add item to order
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotals();
    }

    /**
     * Remove item from order
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        calculateTotals();
    }

    /**
     * Get remaining amount to pay
     */
    public BigDecimal getRemainingAmount() {
        BigDecimal paid = totalPaid != null ? totalPaid : BigDecimal.ZERO;
        return totalAmount.subtract(paid);
    }

    /**
     * Check if fully paid
     */
    public boolean isFullyPaid() {
        return getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * Confirm order
     */
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }

        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();

        addHistory("Order confirmed", null);
    }

    /**
     * Start processing
     */
    public void startProcessing() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed orders can be processed");
        }

        this.status = OrderStatus.PROCESSING;
        this.processingAt = LocalDateTime.now();

        addHistory("Order processing started", null);
    }

    /**
     * Ship order
     */
    public void ship(String trackingNumber) {
        if (status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Only processing orders can be shipped");
        }

        this.status = OrderStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
        this.trackingNumber = trackingNumber;

        addHistory("Order shipped", "Tracking: " + trackingNumber);
    }

    /**
     * Deliver order
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be delivered");
        }

        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();

        addHistory("Order delivered", null);
    }

    /**
     * Complete order
     */
    public void complete() {
        if (status != OrderStatus.DELIVERED && status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order cannot be completed in current status");
        }

        this.status = OrderStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();

        addHistory("Order completed", null);
    }

    /**
     * Cancel order
     */
    public void cancel(User cancelledBy, String reason) {
        if (status == OrderStatus.COMPLETED || status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel completed or delivered orders");
        }

        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;

        addHistory("Order cancelled", reason);
    }

    /**
     * Assign to staff
     */
    public void assignTo(User staff) {
        this.assignedTo = staff;
        this.assignedAt = LocalDateTime.now();

        addHistory("Order assigned to " + staff.getName(), null);
    }

    /**
     * Apply promotion
     */
    public void applyPromotion(String code, BigDecimal discount) {
        this.promotionCode = code;
        this.promotionDiscount = discount;
        calculateTotals();

        addHistory("Promotion applied", "Code: " + code);
    }

    /**
     * Redeem loyalty points
     */
    public void redeemPoints(Integer points, BigDecimal value) {
        this.pointsRedeemed = points;
        this.pointsValue = value;
        calculateTotals();

        addHistory("Loyalty points redeemed", points + " points");
    }

    /**
     * Add payment
     */
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setOrder(this);
        updatePaymentStatus();
    }

    /**
     * Update payment status
     */
    public void updatePaymentStatus() {
        BigDecimal paid = totalPaid != null ? totalPaid : BigDecimal.ZERO;

        if (paid.compareTo(BigDecimal.ZERO) == 0) {
            this.paymentStatus = PaymentStatus.UNPAID;
        } else if (paid.compareTo(totalAmount) >= 0) {
            this.paymentStatus = PaymentStatus.PAID;
        } else {
            this.paymentStatus = PaymentStatus.PARTIAL;
        }
    }

    /**
     * Add history entry
     */
    public void addHistory(String action, String notes) {
        OrderHistory history = OrderHistory.builder()
                .order(this)
                .oldStatus(this.status)
                .newStatus(this.status)
                .action(action)
                .notes(notes)
                .build();

        histories.add(history);
    }

    /**
     * Check if can be cancelled
     */
    public boolean canBeCancelled() {
        return status != OrderStatus.COMPLETED &&
                status != OrderStatus.DELIVERED &&
                status != OrderStatus.CANCELLED;
    }

    /**
     * Check if can be edited
     */
    public boolean canBeEdited() {
        return status == OrderStatus.PENDING;
    }

    /**
     * Check if requires shipping
     */
    public boolean requiresShipping() {
        return !"PICKUP".equals(shippingMethod);
    }

    /**
     * Get order age in days
     */
    public Long getOrderAgeInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(
                getCreatedAt().toLocalDate(),
                LocalDateTime.now().toLocalDate());
    }

    /**
     * Check if overdue
     */
    public boolean isOverdue() {
        return expectedDeliveryDate != null &&
                LocalDateTime.now().isAfter(expectedDeliveryDate) &&
                status != OrderStatus.DELIVERED &&
                status != OrderStatus.COMPLETED &&
                status != OrderStatus.CANCELLED;
    }

    /**
     * Get full order info
     */
    public String getFullInfo() {
        return String.format("%s | %s | %s | %s",
                orderNumber,
                customerName,
                totalAmount,
                status);
    }
}
