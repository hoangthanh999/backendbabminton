package com.badminton.entity.equipment;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_assignments", indexes = {
        @Index(name = "idx_equipment_status", columnList = "equipment_id, status"),
        @Index(name = "idx_user_status", columnList = "user_id, status"),
        @Index(name = "idx_dates", columnList = "assigned_date, returned_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentAssignment extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Staff member assigned to

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.ACTIVE;

    @Column(name = "assigned_date", nullable = false)
    @Builder.Default
    private LocalDate assignedDate = LocalDate.now();

    @Column(name = "expected_return_date")
    private LocalDate expectedReturnDate;

    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;

    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "condition_on_assignment", length = 20)
    private String conditionOnAssignment;

    @Column(name = "condition_on_return", length = 20)
    private String conditionOnReturn;

    @Column(name = "return_notes", columnDefinition = "TEXT")
    private String returnNotes;

    @Column(name = "is_damaged")
    @Builder.Default
    private Boolean isDamaged = false;

    @Column(name = "damage_description", columnDefinition = "TEXT")
    private String damageDescription;

    // Helper Methods

    /**
     * Return equipment
     */
    public void returnEquipment(User receivedBy, String condition, String notes) {
        this.status = AssignmentStatus.RETURNED;
        this.returnedDate = LocalDate.now();
        this.receivedBy = receivedBy;
        this.conditionOnReturn = condition;
        this.returnNotes = notes;
    }

    /**
     * Report damage
     */
    public void reportDamage(String description) {
        this.isDamaged = true;
        this.damageDescription = description;
    }

    /**
     * Check if overdue
     */
    public boolean isOverdue() {
        return status == AssignmentStatus.ACTIVE &&
                expectedReturnDate != null &&
                LocalDate.now().isAfter(expectedReturnDate);
    }

    /**
     * Get assignment duration in days
     */
    public Long getAssignmentDuration() {
        LocalDate endDate = returnedDate != null ? returnedDate : LocalDate.now();
        return java.time.temporal.ChronoUnit.DAYS.between(assignedDate, endDate);
    }

    /**
     * Get days overdue
     */
    public Long getDaysOverdue() {
        if (!isOverdue()) {
            return 0L;
        }
        return java.time.temporal.ChronoUnit.DAYS
                .between(expectedReturnDate, LocalDate.now());
    }
}
