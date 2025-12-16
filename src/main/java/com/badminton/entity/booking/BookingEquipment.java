package com.badminton.entity.booking;

import com.badminton.entity.bas

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_equipment", indexes = {
        @Index(name = "idx_booking", columnList = "booking_id"),
        @Index(name = "idx_equipment", columnList = "equipment_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEquipment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "notes")
    private String notes;

    @Column(name = "returned")
    @Builder.Default
    private Boolean returned = false;

    @Column(name = "condition_on_return")
    private String conditionOnReturn;

    @Column(name = "damage_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal damageFee = BigDecimal.ZERO;

    // Lifecycle
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    // Helper Methods
    public void markAsReturned(String condition) {
        this.returned = true;
        this.conditionOnReturn = condition;
    }

    public void applyDamageFee(BigDecimal fee) {
        this.damageFee = fee;
        this.totalPrice = this.totalPrice.add(fee);
    }
}
