package com.badminton.dto.request.tournament;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Register for tournament request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTournamentRequest {

    @NotNull(message = "Tournament ID không được để trống")
    private Long tournamentId;

    @NotBlank(message = "Tên người chơi không được để trống")
    private String playerName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String playerEmail;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String playerPhone;

    private Integer skillLevel; // 1-5

    private String previousExperience;

    // For doubles
    private Long partnerId;
    private String partnerName;
    private String partnerEmail;
    private String partnerPhone;

    // For team
    private Long teamId;

    private String emergencyContact;

    private String emergencyPhone;

    private String medicalInfo;

    private Boolean agreedToTerms;
}
