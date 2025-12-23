package com.badminton.dto.response.court;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Court response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourtResponse {

    private Long id;
    private String name;
    private String courtNumber;
    private String slug;

    private BranchInfo branch;
    private CourtTypeInfo courtType;

    private String location;
    private String description;
    private String status;

    private BigDecimal defaultPrice;
    private BigDecimal peakHourPrice;
    private BigDecimal offPeakPrice;

    private Boolean isIndoor;
    private Boolean hasLighting;
    private Integer capacity;

    private String amenities;
    private List<String> features;

    private String primaryImage;
    private List<String> images;

    private Integer totalBookings;
    private BigDecimal averageRating;
    private Integer reviewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchInfo {
        private Long id;
        private String name;
        private String code;
        private String address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourtTypeInfo {
        private Long id;
        private String name;
        private String description;
    }
}
