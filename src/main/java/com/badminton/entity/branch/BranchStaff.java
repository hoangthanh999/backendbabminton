package com.badminton.entity.branch;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.Department;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "branch_staff", uniqueConstraints = @UniqueConstraint(name = "uk_branch_user_active", columnNames = {
        "branch_id", "user_id", "is_active" }), indexes = {
                @Index(name = "idx_branch_active", columnList = "branch_id, is_active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchStaff extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "position", length = 100)
    private String position; // Manager, Staff, Trainer

    @Enumerated(EnumType.STRING)
    @Column(name = "department", length = 30)
    private Department department;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "salary", precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate; // Percentage

    // Helper methods
    public boolean isCurrentlyEmployed() {
        return isActive && (endDate == null || endDate.isAfter(LocalDate.now()));
    }
}
