package com.badminton.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User profile with statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;

    // Statistics
    private Integer totalBookings;
    private BigDecimal totalSpent;
    private Integer totalOrders;
    private LocalDate lastBookingDate;

    // Loyalty
    private LoyaltyInfo loyalty;

    // Membership
    private LocalDateTime memberSince;
    private String membershipTier;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoyaltyInfo {
        private Integer totalPoints;
        private Integer availablePoints;
        private Integer lifetimePoints;
        private String tier;
        private LocalDate tierExpiresAt;
    }
}
