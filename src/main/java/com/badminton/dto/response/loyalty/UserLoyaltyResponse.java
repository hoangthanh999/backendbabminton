package com.badminton.dto.response.loyalty;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User loyalty summary response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoyaltyResponse {

    private Long id;

    private UserInfo user;
    private TierInfo tier;

    private Integer totalPoints;
    private Integer availablePoints;
    private Integer lifetimePoints;
    private Integer redeemedPoints;
    private Integer expiredPoints;

    private BigDecimal totalSpending;

    private LocalDate memberSince;
    private LocalDate tierExpiresAt;
    private LocalDateTime tierUpdatedAt;

    private Integer pointsToNextTier;
    private TierInfo nextTier;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TierInfo {
        private Long id;
        private String name;
        private Integer level;
        private String color;
        private String icon;
        private BigDecimal discountRate;
        private Integer pointsMultiplier;
    }
}
