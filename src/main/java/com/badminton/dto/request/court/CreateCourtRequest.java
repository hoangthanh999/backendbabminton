package com.badminton.dto.request.court;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Create court request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourtRequest {

    @NotBlank(message = "Tên sân không được để trống")
    @Size(min = 2, max = 100, message = "Tên sân phải từ 2-100 ký tự")
    private String name;

    @NotBlank(message = "Mã sân không được để trống")
    private String courtNumber;

    @NotNull(message = "Chi nhánh không được để trống")
    private Long branchId;

    @NotNull(message = "Loại sân không được để trống")
    private Long courtTypeId;

    private String location;

    private String description;

    @NotNull(message = "Giá mặc định không được để trống")
    @DecimalMin(value = "0.0", message = "Giá phải lớn hơn 0")
    private BigDecimal defaultPrice;

    @NotNull(message = "Trạng thái không được để trống")
    private String status;

    private Boolean isIndoor;

    private Boolean hasLighting;

    private Integer capacity;

    private String amenities;

    private String maintenanceNotes;
}
