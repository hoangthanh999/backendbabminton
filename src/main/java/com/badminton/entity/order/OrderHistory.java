package com.badminton.entity.order;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_history", indexes = {
        @Index(name = "idx_order", columnList = "order_id"),
        @Index(name = "idx_created", columnList = "created_at"),
        @Index(name = "idx_user", columnList = "changed_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private OrderStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private OrderStatus newStatus;

    @Column(name = "action", nullable = false)
    private String action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    // Helper Methods

    public String getChangeDescription() {
        if (oldStatus == null) {
            return String.format("Order created with status: %s", newStatus);
        }
        return String.format("Status changed from %s to %s", oldStatus, newStatus);
    }

    public boolean isStatusChange() {
        return oldStatus != null && !oldStatus.equals(newStatus);
    }
}
