package com.badminton.dto.response.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private Long id;
    private String paymentNumber;

    private BookingInfo booking;
    private PaymentMethodInfo paymentMethod;

    private BigDecimal amount;
    private String paymentType;
    private String status;

    private String transactionId;
    private String gatewayResponse;

    private String notes;

    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfo {
        private Long id;
        private String bookingNumber;
        private String customerName;
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodInfo {
        private Long id;
        private String name;
        private String type;
        private String icon;
    }
}
