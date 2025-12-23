package com.badminton.dto.request.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Payment gateway callback request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackRequest {

    private String transactionId;
    private String status;
    private String responseCode;
    private String message;
    private Map<String, String> additionalData;
}
