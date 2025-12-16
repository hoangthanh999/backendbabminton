package com.badminton.entity.court;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.enums.MaintenanceStatus;
import com.badminton.enums.MaintenanceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "court_maintenance", indexes = {
        @Index(name = "idx_court", columnList = "court_id"),
        @Index(name = "idx_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_court_dates", columnList = "court_id, start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtMaintenance extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 20)
    private MaintenanceType maintenanceType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED;

    @Column(name = "cost", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "performed_by")
    private String performedBy; // Company or person name

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completion_notes", columnDefinition = "TEXT")
    private String completionNotes;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    // Helper Methods

    /**
     * Check if maintenance is ongoing
     */
    public boolean isOngoing() {
        LocalDate today = LocalDate.now();
        return status == MaintenanceStatus.IN_PROGRESS
                && !today.isBefore(startDate)
                && !today.isAfter(endDate);
    }

    /**
     * Check if maintenance is scheduled for future
     */
    public boolean isScheduled() {
        return status == MaintenanceStatus.SCHEDULED
                && startDate.isAfter(LocalDate.now());
    }

    /**
     * Check if maintenance is overdue
     */
    public boolean isOverdue() {
        return (status == MaintenanceStatus.SCHEDULED || status == MaintenanceStatus.IN_PROGRESS)
                && endDate.isBefore(LocalDate.now());
    }

    /**
     * Get duration in days
     */
    public long getDurationInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Get actual duration if completed
     */
    public Long getActualDurationInDays() {
        if (actualEndDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(startDate, actualEndDate) + 1;
        }
        return null;
    }

    /**
     * Check if date is within maintenance period
     */
    public boolean isDateInMaintenancePeriod(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Start maintenance
     */
    public void start() {
        if (status == MaintenanceStatus.SCHEDULED) {
            this.status = MaintenanceStatus.IN_PROGRESS;
        }
    }

    /**
     * Complete maintenance
     */
    public void complete(String completionNotes) {
        this.status = MaintenanceStatus.COMPLETED;
        this.actualEndDate = LocalDate.now();
        this.completionNotes = completionNotes;
    }

    /**
     * Cancel maintenance
     */
    public void cancel(String reason) {
        this.status = MaintenanceStatus.CANCELLED;
        this.notes = (notes != null ? notes + "\n" : "") + "Cancelled: " + reason;
    }

    /**
     * Get cost per day
     */
    public BigDecimal getCostPerDay() {
        long days = getDurationInDays();
        return days > 0 ? cost.divide(BigDecimal.valueOf(days), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
}
