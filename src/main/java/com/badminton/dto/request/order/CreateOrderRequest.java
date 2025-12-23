package com.badminton.dto.request.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Create order request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private Long userId;

    @NotNull(message = "Chi nhánh không được để trống")
    private Long branchId;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<OrderItemRequest> items;

    private String orderType; // RETAIL, SERVICE, RENTAL

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String deliveryAddress;

    private String notes;

    // Pricing
    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal tax;

    private BigDecimal shippingFee;

    private BigDecimal totalAmount;

    // Payment
    private Long paymentMethodId;

    private String promotionCode;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {

        @NotNull(message = "Product/Service ID không được để trống")
        private Long itemId;

        @NotBlank(message = "Item type không được để trống")
        private String itemType; // PRODUCT, SERVICE

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải >= 1")
        private Integer quantity;

        @NotNull(message = "Đơn giá không được để trống")
        @DecimalMin(value = "0.0", message = "Đơn giá phải >= 0")
        private BigDecimal unitPrice;

        private BigDecimal discount;

        private String notes;
    }
}
