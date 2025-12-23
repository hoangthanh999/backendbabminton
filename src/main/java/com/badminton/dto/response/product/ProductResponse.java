package com.badminton.dto.response.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Product response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private Long id;
    private String name;
    private String productCode;
    private String slug;

    private CategoryInfo category;
    private BranchInfo branch;

    private String description;
    private String specifications;

    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal discountPrice;
    private BigDecimal finalPrice;
    private Integer discountPercent;

    private Integer stockQuantity;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK

    private String unit;
    private String brand;
    private String model;

    private String primaryImage;
    private List<String> images;
    private List<String> tags;

    private Boolean isActive;
    private Boolean isFeatured;

    private Integer totalSold;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private Integer viewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String slug;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchInfo {
        private Long id;
        private String name;
        private String code;
    }
}
