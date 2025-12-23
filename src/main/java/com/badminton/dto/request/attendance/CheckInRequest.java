package com.badminton.dto.request.attendance;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Check-in request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Branch ID không được để trống")
    private Long branchId;

    private LocalDate date;

    private LocalTime checkInTime;

    private String location;

    private String deviceInfo;

    private String notes;
}
