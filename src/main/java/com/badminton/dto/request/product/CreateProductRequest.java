package com.badminton.dto.request.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Create product request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 200, message = "Tên sản phẩm phải từ 2-200 ký tự")
    private String name;

    @NotBlank(message = "Mã sản phẩm không được để trống")
    private String productCode;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "Chi nhánh không được để trống")
    private Long branchId;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 5000, message = "Mô tả không quá 5000 ký tự")
    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", message = "Giá phải >= 0")
    private BigDecimal price;

    private BigDecimal costPrice;

    private BigDecimal discountPrice;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải >= 0")
    private Integer stockQuantity;

    private Integer minStockLevel;

    private Integer maxStockLevel;

    private String unit; // PIECE, BOX, SET, PAIR

    private String brand;

    private String model;

    private String specifications;

    private List<String> images;

    private List<String> tags;

    private Boolean isActive;

    private Boolean isFeatured;

    private Integer displayOrder;
}
