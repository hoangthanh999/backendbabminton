package com.badminton.entity.attendance;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.user.User;
import com.badminton.enums.AttendanceStatus;
import com.badminton.enums.AttendanceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendances", uniqueConstraints = @UniqueConstraint(name = "uk_user_date", columnNames = { "user_id",
        "date" }), indexes = {
                @Index(name = "idx_branch_date", columnList = "branch_id, date"),
                @Index(name = "idx_user_date", columnList = "user_id, date"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_date", columnList = "date")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", nullable = false, length = 20)
    @Builder.Default
    private AttendanceType attendanceType = AttendanceType.WORK;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    // Check-in/Check-out
    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "check_in_location")
    private String checkInLocation;

    @Column(name = "check_out_location")
    private String checkOutLocation;

    @Column(name = "check_in_ip", length = 45)
    private String checkInIp;

    @Column(name = "check_out_ip", length = 45)
    private String checkOutIp;

    @Column(name = "check_in_device")
    private String checkInDevice;

    @Column(name = "check_out_device")
    private String checkOutDevice;

    // Scheduled Times
    @Column(name = "scheduled_start_time")
    private LocalTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private LocalTime scheduledEndTime;

    // Work Hours
    @Column(name = "total_hours", precision = 5, scale = 2)
    private BigDecimal totalHours;

    @Column(name = "regular_hours", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal regularHours = BigDecimal.ZERO;

    @Column(name = "overtime_hours", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "break_hours", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal breakHours = BigDecimal.ZERO;

    // Late/Early
    @Column(name = "is_late")
    @Builder.Default
    private Boolean isLate = false;

    @Column(name = "late_minutes")
    @Builder.Default
    private Integer lateMinutes = 0;

    @Column(name = "is_early_leave")
    @Builder.Default
    private Boolean isEarlyLeave = false;

    @Column(name = "early_leave_minutes")
    @Builder.Default
    private Integer earlyLeaveMinutes = 0;

    // Leave Details
    @Column(name = "leave_type", length = 50)
    private String leaveType; // SICK, ANNUAL, PERSONAL, UNPAID

    @Column(name = "leave_reason", columnDefinition = "TEXT")
    private String leaveReason;

    @Column(name = "leave_approved")
    @Builder.Default
    private Boolean leaveApproved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_approved_by")
    private User leaveApprovedBy;

    @Column(name = "leave_approved_at")
    private LocalDateTime leaveApprovedAt;

    // Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    // Verification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    // Helper Methods

    /**
     * Check in
     */
    public void checkIn(LocalTime time, String location, String ip, String device) {
        this.checkInTime = time;
        this.checkInLocation = location;
        this.checkInIp = ip;
        this.checkInDevice = device;

        // Check if late
        if (scheduledStartTime != null && time.isAfter(scheduledStartTime)) {
            this.isLate = true;
            this.lateMinutes = (int) java.time.Duration.between(scheduledStartTime, time).toMinutes();
        }
    }

    /**
     * Check out
     */
    public void checkOut(LocalTime time, String location, String ip, String device) {
        this.checkOutTime = time;
        this.checkOutLocation = location;
        this.checkOutIp = ip;
        this.checkOutDevice = device;

        // Check if early leave
        if (scheduledEndTime != null && time.isBefore(scheduledEndTime)) {
            this.isEarlyLeave = true;
            this.earlyLeaveMinutes = (int) java.time.Duration.between(time, scheduledEndTime).toMinutes();
        }

        // Calculate total hours
        calculateTotalHours();
    }

    /**
     * Calculate total hours worked
     */
    public void calculateTotalHours() {
        if (checkInTime != null && checkOutTime != null) {
            long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();

            // Subtract break hours
            if (breakHours != null) {
                minutes -= breakHours.multiply(new BigDecimal("60")).intValue();
            }

            this.totalHours = new BigDecimal(minutes).divide(
                    new BigDecimal("60"),
                    2,
                    java.math.RoundingMode.HALF_UP);

            // Calculate regular and overtime hours
            BigDecimal standardHours = new BigDecimal("8");

            if (totalHours.compareTo(standardHours) <= 0) {
                this.regularHours = totalHours;
                this.overtimeHours = BigDecimal.ZERO;
            } else {
                this.regularHours = standardHours;
                this.overtimeHours = totalHours.subtract(standardHours);
            }
        }
    }

    /**
     * Mark as absent
     */
    public void markAsAbsent(String reason) {
        this.status = AttendanceStatus.ABSENT;
        this.notes = reason;
    }

    /**
     * Request leave
     */
    public void requestLeave(String leaveType, String reason) {
        this.status = AttendanceStatus.ON_LEAVE;
        this.leaveType = leaveType;
        this.leaveReason = reason;
        this.leaveApproved = false;
    }

    /**
     * Approve leave
     */
    public void approveLeave(User approver) {
        if (status != AttendanceStatus.ON_LEAVE) {
            throw new IllegalStateException("Only leave requests can be approved");
        }

        this.leaveApproved = true;
        this.leaveApprovedBy = approver;
        this.leaveApprovedAt = LocalDateTime.now();
    }

    /**
     * Verify attendance
     */
    public void verify(User verifier) {
        this.isVerified = true;
        this.verifiedBy = verifier;
        this.verifiedAt = LocalDateTime.now();
    }

    /**
     * Check if complete
     */
    public boolean isComplete() {
        return checkInTime != null && checkOutTime != null;
    }

    /**
     * Get work duration display
     */
    public String getWorkDurationDisplay() {
        if (totalHours == null) {
            return "N/A";
        }

        int hours = totalHours.intValue();
        int minutes = totalHours.subtract(new BigDecimal(hours))
                .multiply(new BigDecimal("60"))
                .intValue();

        return String.format("%d giờ %d phút", hours, minutes);
    }
}
