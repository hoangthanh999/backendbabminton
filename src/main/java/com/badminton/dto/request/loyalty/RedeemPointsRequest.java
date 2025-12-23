package com.badminton.dto.request.loyalty;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Redeem loyalty points request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemPointsRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Số điểm không được để trống")
    @Min(value = 1, message = "Số điểm phải >= 1")
    private Integer points;

    @NotBlank(message = "Loại đổi điểm không được để trống")
    private String redeemType; // DISCOUNT, VOUCHER, GIFT, CASH

    private Long rewardId;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;
}
