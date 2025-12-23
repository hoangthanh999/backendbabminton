package com.badminton.dto.request.payment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Create payment request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Booking ID không được để trống")
    private Long bookingId;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private Long paymentMethodId;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    private String paymentType; // FULL, DEPOSIT, REMAINING

    private String transactionId;

    private String notes;

    // For online payment
    private String returnUrl;
    private String cancelUrl;
    private String ipAddress;
}
