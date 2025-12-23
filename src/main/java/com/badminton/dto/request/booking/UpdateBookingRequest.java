package com.badminton.dto.request.booking;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Update booking request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingRequest {

    private Long courtId;

    @Future(message = "Ngày đặt phải là ngày trong tương lai")
    private LocalDate date;

    private LocalTime timeStart;

    private LocalTime timeEnd;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String notes;

    private String specialRequests;

    private String status;

    private BigDecimal totalAmount;

    private BigDecimal depositAmount;
}
