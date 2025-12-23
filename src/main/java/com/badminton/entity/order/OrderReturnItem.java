package com.badminton.entity.order;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_return_items", indexes = {
        @Index(name = "idx_return", columnList = "return_id"),
        @Index(name = "idx_order_item", columnList = "order_item_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderReturnItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "return_id", nullable = false)
    private OrderReturn orderReturn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "condition_notes", columnDefinition = "TEXT")
    private String conditionNotes;

    @Column(name = "is_restockable")
    @Builder.Default
    private Boolean isRestockable = true;
}
