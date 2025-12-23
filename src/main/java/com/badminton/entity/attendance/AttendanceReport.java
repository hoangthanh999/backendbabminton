package com.badminton.entity.attendance;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "attendance_reports", uniqueConstraints = @UniqueConstraint(name = "uk_user_month", columnNames = {
        "user_id", "year", "month" }), indexes = {
                @Index(name = "idx_branch_period", columnList = "branch_id, year, month"),
                @Index(name = "idx_user_period", columnList = "user_id, year, month")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "total_working_days")
    @Builder.Default
    private Integer totalWorkingDays = 0;

    @Column(name = "days_present")
    @Builder.Default
    private Integer daysPresent = 0;

    @Column(name = "days_absent")
    @Builder.Default
    private Integer daysAbsent = 0;

    @Column(name = "days_on_leave")
    @Builder.Default
    private Integer daysOnLeave = 0;

    @Column(name = "days_late")
    @Builder.Default
    private Integer daysLate = 0;

    @Column(name = "total_late_minutes")
    @Builder.Default
    private Integer totalLateMinutes = 0;

    @Column(name = "total_hours_worked", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal totalHoursWorked = BigDecimal.ZERO;

    @Column(name = "regular_hours", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal regularHours = BigDecimal.ZERO;

    @Column(name = "overtime_hours", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "attendance_rate", precision = 5, scale = 2)
    private BigDecimal attendanceRate;

    @Column(name = "punctuality_rate", precision = 5, scale = 2)
    private BigDecimal punctualityRate;

    // Helper Methods

    /**
     * Calculate attendance rate
     */
    public void calculateAttendanceRate() {
        if (totalWorkingDays == 0) {
            this.attendanceRate = BigDecimal.ZERO;
            return;
        }

        this.attendanceRate = BigDecimal.valueOf(daysPresent)
                .divide(BigDecimal.valueOf(totalWorkingDays), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Calculate punctuality rate
     */
    public void calculatePunctualityRate() {
        if (daysPresent == 0) {
            this.punctualityRate = BigDecimal.ZERO;
            return;
        }

        int punctualDays = daysPresent - daysLate;
        this.punctualityRate = BigDecimal.valueOf(punctualDays)
                .divide(BigDecimal.valueOf(daysPresent), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Get period display
     */
    public String getPeriodDisplay() {
        return String.format("%02d/%d", month, year);
    }
}
