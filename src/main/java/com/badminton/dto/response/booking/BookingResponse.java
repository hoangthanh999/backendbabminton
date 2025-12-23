package com.badminton.dto.response.booking;

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
 * Booking response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {

    private Long id;
    private String bookingNumber;

    private CourtInfo court;
    private UserInfo user;

    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private Integer durationMinutes;

    private String bookingType;
    private String status;
    private String paymentStatus;

    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;

    private String customerName;
    private String customerPhone;
    private String customerEmail;

    private String notes;
    private String specialRequests;

    private Boolean isRecurring;
    private Integer recurringWeeks;

    private String qrCode;
    private String checkInCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourtInfo {
        private Long id;
        private String name;
        private String courtNumber;
        private String type;
        private String location;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private String phone;
    }
}
