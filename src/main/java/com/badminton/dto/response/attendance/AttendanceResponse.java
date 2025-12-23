package com.badminton.dto.response.attendance;

import com.badminton.enums.AttendanceStatus;
import com.badminton.enums.AttendanceType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Attendance response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceResponse {

    private Long id;

    private UserInfo user;
    private BranchInfo branch;

    private LocalDate date;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;

    private AttendanceStatus status;
    private AttendanceType attendanceType;

    private Boolean isLate;
    private Integer lateMinutes;

    private Boolean isEarlyLeave;
    private Integer earlyLeaveMinutes;

    private BigDecimal totalHours;
    private BigDecimal overtimeHours;

    private String checkInLocation;
    private String checkOutLocation;

    private String notes;

    // Leave information
    private String leaveType;
    private String leaveReason;
    private Boolean leaveApproved;
    private UserInfo leaveApprovedBy;
    private LocalDateTime leaveApprovedAt;

    private Boolean isVerified;
    private UserInfo verifiedBy;
    private LocalDateTime verifiedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private String avatar;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchInfo {
        private Long id;
        private String name;
        private String code;
    }
}
