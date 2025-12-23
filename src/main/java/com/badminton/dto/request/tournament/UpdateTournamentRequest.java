package com.badminton.dto.request.tournament;

import com.badminton.enums.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Update tournament request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTournamentRequest {

    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate registrationStart;

    private LocalDate registrationEnd;

    private Integer minParticipants;

    private Integer maxParticipants;

    private BigDecimal entryFee;

    private BigDecimal prizePool;

    private String rules;

    private String eligibilityCriteria;

    private String venue;

    private String contactInfo;

    private TournamentStatus status;

    private Boolean isPublic;

    private Boolean isFeatured;

    private String bannerImage;

    private String posterImage;
}
