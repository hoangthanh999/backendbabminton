package com.badminton.dto.request.tournament;

import com.badminton.enums.TournamentFormat;
import com.badminton.enums.TournamentType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Create tournament request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTournamentRequest {

    @NotBlank(message = "Tên giải đấu không được để trống")
    @Size(min = 3, max = 200, message = "Tên giải đấu phải từ 3-200 ký tự")
    private String name;

    @NotBlank(message = "Mã giải đấu không được để trống")
    private String tournamentCode;

    @NotNull(message = "Chi nhánh không được để trống")
    private Long branchId;

    @NotNull(message = "Loại giải đấu không được để trống")
    private TournamentType tournamentType;

    @NotNull(message = "Thể thức không được để trống")
    private TournamentFormat tournamentFormat;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 5000, message = "Mô tả không quá 5000 ký tự")
    private String description;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @Future(message = "Ngày bắt đầu phải là ngày trong tương lai")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @NotNull(message = "Ngày bắt đầu đăng ký không được để trống")
    private LocalDate registrationStart;

    @NotNull(message = "Ngày kết thúc đăng ký không được để trống")
    private LocalDate registrationEnd;

    @NotNull(message = "Số người tối thiểu không được để trống")
    @Min(value = 2, message = "Số người tối thiểu phải từ 2 trở lên")
    private Integer minParticipants;

    @NotNull(message = "Số người tối đa không được để trống")
    @Min(value = 2, message = "Số người tối đa phải từ 2 trở lên")
    private Integer maxParticipants;

    @NotNull(message = "Lệ phí tham gia không được để trống")
    @DecimalMin(value = "0.0", message = "Lệ phí phải >= 0")
    private BigDecimal entryFee;

    private BigDecimal prizePool;

    private String rules;

    private String eligibilityCriteria;

    private String venue;

    private String contactInfo;

    private Boolean isPublic;

    private Boolean isFeatured;

    private String bannerImage;

    private String posterImage;

    private Long organizerId;
}
