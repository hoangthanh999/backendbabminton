package com.badminton.entity.court;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.booking.Booking;
import com.badminton.enums.CourtStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courts", indexes = {
        @Index(name = "idx_branch_status", columnList = "branch_id, status"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_court_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Court extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // Sân 1, Sân 2, Court A, Court B

    @Column(name = "location", length = 255)
    private String location; // Tầng 1, Khu A, Building B

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CourtStatus status = CourtStatus.ACTIVE;

    // Court Specifications
    @Column(name = "court_type", length = 50)
    @Builder.Default
    private String courtType = "STANDARD"; // STANDARD, VIP, PREMIUM, TOURNAMENT

    @Column(name = "floor_type", length = 50)
    private String floorType; // Wood, Synthetic, PU

    @Column(name = "has_air_conditioning")
    @Builder.Default
    private Boolean hasAirConditioning = false;

    @Column(name = "has_lighting")
    @Builder.Default
    private Boolean hasLighting = true;

    @Column(name = "has_scoreboard")
    @Builder.Default
    private Boolean hasScoreboard = false;

    @Column(name = "max_players")
    @Builder.Default
    private Integer maxPlayers = 4; // Singles: 2, Doubles: 4

    @Column(name = "width_meters", precision = 5, scale = 2)
    private Double widthMeters; // 6.1m standard

    @Column(name = "length_meters", precision = 5, scale = 2)
    private Double lengthMeters; // 13.4m standard

    @Column(name = "ceiling_height_meters", precision = 5, scale = 2)
    private Double ceilingHeightMeters; // Min 9m for international

    // Display Order
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    // Metadata
    @Column(name = "amenities", columnDefinition = "JSON")
    private String amenities; // JSON: ["Shower", "Locker", "Water Dispenser"]

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Calculated fields (not stored in DB)
    @Formula("(SELECT COUNT(*) FROM court_images ci WHERE ci.court_id = id)")
    private Integer imageCount;

    @Formula("(SELECT AVG(f.rating) FROM feedback f " +
            "JOIN bookings b ON f.booking_id = b.id " +
            "WHERE b.court_id = id)")
    private Double averageRating;

    // Relationships
    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC, id ASC")
    @Builder.Default
    private List<CourtImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CourtPricing> pricings = new HashSet<>();

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Schedule> schedules = new HashSet<>();

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CourtMaintenance> maintenances = new HashSet<>();

    @OneToMany(mappedBy = "court")
    @Builder.Default
    private Set<Booking> bookings = new HashSet<>();

    // Helper Methods

    /**
     * Add image to court
     */
    public void addImage(CourtImage image) {
        images.add(image);
        image.setCourt(this);

        // If this is the first image, make it primary
        if (images.size() == 1) {
            image.setIsPrimary(true);
        }
    }

    /**
     * Remove image from court
     */
    public void removeImage(CourtImage image) {
        images.remove(image);
        image.setCourt(null);

        // If removed image was primary, set first image as primary
        if (image.getIsPrimary() && !images.isEmpty()) {
            images.get(0).setIsPrimary(true);
        }
    }

    /**
     * Get primary image
     */
    public CourtImage getPrimaryImage() {
        return images.stream()
                .filter(CourtImage::getIsPrimary)
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }

    /**
     * Set image as primary
     */
    public void setPrimaryImage(CourtImage newPrimary) {
        images.forEach(img -> img.setIsPrimary(false));
        newPrimary.setIsPrimary(true);
    }

    /**
     * Add pricing
     */
    public void addPricing(CourtPricing pricing) {
        pricings.add(pricing);
        pricing.setCourt(this);
    }

    /**
     * Add schedule
     */
    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setCourt(this);
    }

    /**
     * Check if court is available
     */
    public boolean isAvailable() {
        return status == CourtStatus.ACTIVE;
    }

    /**
     * Check if court is under maintenance
     */
    public boolean isUnderMaintenance() {
        return status == CourtStatus.MAINTENANCE;
    }

    /**
     * Get full name with branch
     */
    public String getFullName() {
        return branch.getName() + " - " + name;
    }

    /**
     * Check if court is VIP
     */
    public boolean isVIP() {
        return "VIP".equalsIgnoreCase(courtType) || "PREMIUM".equalsIgnoreCase(courtType);
    }

    /**
     * Check if court meets international standards
     */
    public boolean meetsInternationalStandards() {
        return widthMeters != null && widthMeters >= 6.1
                && lengthMeters != null && lengthMeters >= 13.4
                && ceilingHeightMeters != null && ceilingHeightMeters >= 9.0;
    }
}
