package com.badminton.dto.response.tournament;

import com.badminton.enums.TournamentFormat;
import com.badminton.enums.TournamentStatus;
import com.badminton.enums.TournamentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tournament response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TournamentResponse {

    private Long id;
    private String name;
    private String tournamentCode;
    private String slug;

    private BranchInfo branch;
    private OrganizerInfo organizer;

    private TournamentType tournamentType;
    private TournamentFormat tournamentFormat;
    private TournamentStatus status;

    private String description;
    private String rules;
    private String eligibilityCriteria;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationStart;
    private LocalDate registrationEnd;

    private Integer minParticipants;
    private Integer maxParticipants;
    private Integer currentParticipants;

    private BigDecimal entryFee;
    private BigDecimal prizePool;
    private BigDecimal totalCollected;

    private String venue;
    private String contactInfo;

    private Boolean isPublic;
    private Boolean isFeatured;

    private String bannerImage;
    private String posterImage;

    private Integer totalMatches;
    private Integer completedMatches;
    private Integer viewCount;

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
    public static class OrganizerInfo {
        private Long id;
        private String name;
        private String email;
        private String phone;
    }
}
