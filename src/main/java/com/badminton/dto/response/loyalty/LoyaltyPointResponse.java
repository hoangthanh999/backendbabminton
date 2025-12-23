package com.badminton.dto.response.loyalty;

import com.badminton.enums.PointTransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Loyalty point transaction response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoyaltyPointResponse {

    private Long id;

    private UserInfo user;

    private Integer points;
    private PointTransactionType transactionType;

    private String referenceType;
    private Long referenceId;

    private String description;

    private LocalDate expiryDate;
    private Boolean isExpired;

    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
    }
}
