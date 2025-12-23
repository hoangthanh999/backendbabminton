package com.badminton.entity.product;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.booking.BookingService;
import com.badminton.enums.ServiceStatus;
import com.badminton.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "services", uniqueConstraints = @UniqueConstraint(name = "uk_slug", columnNames = "slug"), indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_type", columnList = "service_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", length = 50)
    private ServiceType serviceType;

    @Column(name = "price", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ServiceStatus status = ServiceStatus.ACTIVE;

    @Column(name = "icon")
    private String icon;

    @Column(name = "image")
    private String image;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "terms_conditions", columnDefinition = "TEXT")
    private String termsConditions;

    // Relationships
    @OneToMany(mappedBy = "service")
    @Builder.Default
    private Set<BookingService> bookingServices = new HashSet<>();

    // Helper Methods
    public boolean isAvailable() {
        return status == ServiceStatus.ACTIVE;
    }

    public String getFormattedDuration() {
        if (durationMinutes == null)
            return null;

        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;

        if (hours > 0 && minutes > 0) {
            return String.format("%d giờ %d phút", hours, minutes);
        } else if (hours > 0) {
            return String.format("%d giờ", hours);
        } else {
            return String.format("%d phút", minutes);
        }
    }
}
