package com.badminton.dto.request.attendance;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Leave request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Branch ID không được để trống")
    private Long branchId;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @NotBlank(message = "Loại nghỉ không được để trống")
    private String leaveType; // SICK, ANNUAL, PERSONAL, EMERGENCY

    @NotBlank(message = "Lý do không được để trống")
    @Size(min = 10, max = 500, message = "Lý do phải từ 10-500 ký tự")
    private String reason;

    private String attachments;
}
