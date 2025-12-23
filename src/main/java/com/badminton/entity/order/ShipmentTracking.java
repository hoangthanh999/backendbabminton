package com.badminton.entity.order;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipment_tracking", indexes = {
        @Index(name = "idx_shipment", columnList = "shipment_id"),
        @Index(name = "idx_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentTracking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private OrderShipment shipment;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "checkpoint_time")
    private java.time.LocalDateTime checkpointTime;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;
}
