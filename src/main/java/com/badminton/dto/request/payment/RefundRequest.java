package com.badminton.dto.request.payment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Refund request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

    @NotNull(message = "Payment ID không được để trống")
    private Long paymentId;

    @NotNull(message = "Số tiền hoàn không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    private BigDecimal refundAmount;

    @NotBlank(message = "Lý do hoàn tiền không được để trống")
    private String reason;

    private String notes;
}
