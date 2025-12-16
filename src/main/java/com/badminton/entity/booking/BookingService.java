package com.badminton.entity.booking;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.product.Service;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_services", indexes = {
        @Index(name = "idx_booking", columnList = "booking_id"),
        @Index(name = "idx_service", columnList = "service_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingService extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "notes")
    private String notes;

    @Column(name = "completed")
    @Builder.Default
    private Boolean completed = false;

    @Column(name = "staff_notes")
    private String staffNotes;

    // Lifecycle
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    // Helper Methods
    public void markAsCompleted(String notes) {
        this.completed = true;
        this.staffNotes = notes;
    }
}
