package com.badminton.dto.request.booking;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Create booking request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotNull(message = "Sân không được để trống")
    private Long courtId;

    @NotNull(message = "Ngày đặt không được để trống")
    @Future(message = "Ngày đặt phải là ngày trong tương lai")
    private LocalDate date;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime timeStart;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime timeEnd;

    private String bookingType; // SINGLE, RECURRING, EVENT

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String notes;

    private String specialRequests;

    // For recurring bookings
    private Integer recurringWeeks;
    private Integer dayOfWeek;

    // Pricing
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;

    // Promotion
    private String promotionCode;
}
