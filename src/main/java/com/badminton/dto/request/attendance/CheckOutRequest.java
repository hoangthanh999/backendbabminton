package com.badminton.dto.request.attendance;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Check-out request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequest {

    @NotNull(message = "Attendance ID không được để trống")
    private Long attendanceId;

    private LocalTime checkOutTime;

    private String location;

    private String notes;
}
