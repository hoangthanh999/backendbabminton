package com.badminton.entity.equipment;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.booking.BookingEquipment;
import com.badminton.enums.EquipmentCondition;
import com.badminton.enums.EquipmentStatus;
import com.badminton.enums.EquipmentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "equipment", uniqueConstraints = @UniqueConstraint(name = "uk_equipment_code", columnNames = "equipment_code"), indexes = {
        @Index(name = "idx_branch_status", columnList = "branch_id, status"),
        @Index(name = "idx_type_status", columnList = "equipment_type, status"),
        @Index(name = "idx_condition", columnList = "equipment_condition"),
        @Index(name = "idx_available", columnList = "is_available")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE equipment SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Equipment extends AuditableEntity {

    // Core Info
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "equipment_code", unique = true, nullable = false, length = 50)
    private String equipmentCode; // EQ-001, EQ-002

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false, length = 50)
    private EquipmentType equipmentType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_condition", nullable = false, length = 20)
    @Builder.Default
    private EquipmentCondition equipmentCondition = EquipmentCondition.EXCELLENT;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    // Specifications
    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "specifications", columnDefinition = "JSON")
    private String specifications; // JSON: {"weight": "85g", "balance": "head-heavy"}

    // Financial
    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "current_value", precision = 10, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "rental_price_per_hour", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal rentalPricePerHour = BigDecimal.ZERO;

    @Column(name = "rental_price_per_day", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal rentalPricePerDay = BigDecimal.ZERO;

    @Column(name = "deposit_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Column(name = "replacement_cost", precision = 10, scale = 2)
    private BigDecimal replacementCost;

    // Dates
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    // Location & Storage
    @Column(name = "storage_location", length = 100)
    private String storageLocation;

    @Column(name = "rack_number", length = 50)
    private String rackNumber;

    @Column(name = "shelf_number", length = 50)
    private String shelfNumber;

    // Usage Statistics
    @Column(name = "total_rentals")
    @Builder.Default
    private Integer totalRentals = 0;

    @Column(name = "total_rental_hours")
    @Builder.Default
    private Integer totalRentalHours = 0;

    @Column(name = "total_revenue", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "damage_count")
    @Builder.Default
    private Integer damageCount = 0;

    @Column(name = "last_rental_date")
    private LocalDate lastRentalDate;

    // Maintenance
    @Column(name = "maintenance_cost", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal maintenanceCost = BigDecimal.ZERO;

    @Column(name = "maintenance_interval_days")
    @Builder.Default
    private Integer maintenanceIntervalDays = 90; // 3 months

    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;

    // Images
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "images", columnDefinition = "JSON")
    private String images; // JSON array

    // Additional Info
    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_rentable")
    @Builder.Default
    private Boolean isRentable = true;

    @Column(name = "max_rental_duration_hours")
    private Integer maxRentalDurationHours;

    @Column(name = "min_rental_duration_hours")
    @Builder.Default
    private Integer minRentalDurationHours = 1;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Calculated Fields
    @Formula("(SELECT COUNT(*) FROM equipment_assignments ea WHERE ea.equipment_id = id AND ea.status = 'ACTIVE')")
    private Integer activeAssignments;

    @Formula("(SELECT COUNT(*) FROM booking_equipment be WHERE be.equipment_id = id AND be.returned = false)")
    private Integer currentRentals;

    // Relationships
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<EquipmentAssignment> assignments = new HashSet<>();

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<EquipmentMaintenance> maintenances = new HashSet<>();

    @OneToMany(mappedBy = "equipment")
    @Builder.Default
    private Set<BookingEquipment> bookingEquipments = new HashSet<>();

    // Lifecycle
    @PrePersist
    public void prePersist() {
        if (equipmentCode == null) {
            equipmentCode = generateEquipmentCode();
        }

        if (currentValue == null && purchasePrice != null) {
            currentValue = purchasePrice;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateAvailability();
    }

    // Helper Methods

    /**
     * Generate equipment code
     */
    private String generateEquipmentCode() {
        String typePrefix = equipmentType != null ? equipmentType.name().substring(0, 3).toUpperCase() : "EQP";

        return String.format("%s%s%04d",
                typePrefix,
                LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyMMdd")),
                (int) (Math.random() * 10000));
    }

    /**
     * Update availability based on status and assignments
     */
    private void updateAvailability() {
        this.isAvailable = status == EquipmentStatus.AVAILABLE &&
                equipmentCondition != EquipmentCondition.BROKEN &&
                (currentRentals == null || currentRentals == 0) &&
                (activeAssignments == null || activeAssignments == 0);
    }

    /**
     * Check if equipment is available for rental
     */
    public boolean isAvailableForRental() {
        return isAvailable &&
                isRentable &&
                status == EquipmentStatus.AVAILABLE &&
                equipmentCondition != EquipmentCondition.BROKEN;
    }

    /**
     * Check if needs maintenance
     */
    public boolean needsMaintenance() {
        if (nextMaintenanceDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(nextMaintenanceDate) ||
                LocalDate.now().isEqual(nextMaintenanceDate);
    }

    /**
     * Check if warranty is valid
     */
    public boolean isUnderWarranty() {
        return warrantyExpiryDate != null &&
                LocalDate.now().isBefore(warrantyExpiryDate);
    }

    /**
     * Calculate depreciation
     */
    public BigDecimal calculateDepreciation() {
        if (purchasePrice == null || purchaseDate == null) {
            return BigDecimal.ZERO;
        }

        long daysSincePurchase = java.time.temporal.ChronoUnit.DAYS
                .between(purchaseDate, LocalDate.now());

        // Simple straight-line depreciation over 5 years (1825 days)
        BigDecimal depreciationRate = new BigDecimal("0.20"); // 20% per year
        BigDecimal yearsElapsed = BigDecimal.valueOf(daysSincePurchase)
                .divide(new BigDecimal("365"), 4, java.math.RoundingMode.HALF_UP);

        BigDecimal depreciation = purchasePrice
                .multiply(depreciationRate)
                .multiply(yearsElapsed);

        return depreciation.min(purchasePrice);
    }

    /**
     * Calculate current value with depreciation
     */
    public BigDecimal calculateCurrentValue() {
        if (purchasePrice == null) {
            return currentValue != null ? currentValue : BigDecimal.ZERO;
        }

        return purchasePrice.subtract(calculateDepreciation()).max(BigDecimal.ZERO);
    }

    /**
     * Record rental
     */
    public void recordRental(Integer hours, BigDecimal revenue) {
        this.totalRentals++;
        this.totalRentalHours += hours;
        this.totalRevenue = this.totalRevenue.add(revenue);
        this.lastRentalDate = LocalDate.now();
    }

    /**
     * Record damage
     */
    public void recordDamage(EquipmentCondition newCondition, String notes) {
        this.damageCount++;
        this.equipmentCondition = newCondition;
        this.maintenanceNotes = (maintenanceNotes != null ? maintenanceNotes + "\n" : "") +
                LocalDate.now() + ": " + notes;

        if (newCondition == EquipmentCondition.BROKEN) {
            this.status = EquipmentStatus.MAINTENANCE;
            this.isAvailable = false;
        }
    }

    /**
     * Schedule maintenance
     */
    public void scheduleMaintenance(LocalDate date) {
        this.nextMaintenanceDate = date;

        if (LocalDate.now().isEqual(date) || LocalDate.now().isAfter(date)) {
            this.status = EquipmentStatus.MAINTENANCE;
            this.isAvailable = false;
        }
    }

    /**
     * Complete maintenance
     */
    public void completeMaintenance(BigDecimal cost, EquipmentCondition newCondition) {
        this.lastMaintenanceDate = LocalDate.now();
        this.nextMaintenanceDate = LocalDate.now().plusDays(maintenanceIntervalDays);
        this.maintenanceCost = this.maintenanceCost.add(cost);
        this.equipmentCondition = newCondition;

        if (newCondition != EquipmentCondition.BROKEN) {
            this.status = EquipmentStatus.AVAILABLE;
            updateAvailability();
        }
    }

    /**
     * Calculate ROI (Return on Investment)
     */
    public BigDecimal calculateROI() {
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal netProfit = totalRevenue.subtract(maintenanceCost);
        return netProfit.divide(purchasePrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Calculate utilization rate
     */
    public BigDecimal calculateUtilizationRate() {
        if (purchaseDate == null) {
            return BigDecimal.ZERO;
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS
                .between(purchaseDate, LocalDate.now());

        if (totalDays == 0) {
            return BigDecimal.ZERO;
        }

        // Assuming 8 hours per day as maximum utilization
        long maxHours = totalDays * 8;

        return BigDecimal.valueOf(totalRentalHours)
                .divide(BigDecimal.valueOf(maxHours), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Get age in months
     */
    public Long getAgeInMonths() {
        if (purchaseDate == null) {
            return null;
        }

        return java.time.temporal.ChronoUnit.MONTHS
                .between(purchaseDate, LocalDate.now());
    }

    /**
     * Get full equipment info
     */
    public String getFullInfo() {
        return String.format("%s | %s | %s | %s",
                equipmentCode,
                name,
                brand != null ? brand : "N/A",
                status);
    }
}
