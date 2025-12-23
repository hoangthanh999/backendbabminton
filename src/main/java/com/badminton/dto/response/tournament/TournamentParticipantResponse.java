package com.badminton.dto.response.tournament;

import com.badminton.enums.ParticipantStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tournament participant response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TournamentParticipantResponse {

    private Long id;
    private String registrationNumber;

    private TournamentInfo tournament;
    private UserInfo user;
    private TeamInfo team;
    private PartnerInfo partner;

    private String playerName;
    private String playerEmail;
    private String playerPhone;

    private ParticipantStatus status;

    private Integer seedNumber;
    private Integer skillLevel;

    private Boolean isCheckedIn;
    private LocalDateTime checkedInAt;

    private Boolean entryFeePaid;
    private LocalDateTime feePaidAt;

    // Performance
    private Integer matchesPlayed;
    private Integer matchesWon;
    private Integer matchesLost;
    private Integer setsWon;
    private Integer setsLost;
    private Integer pointsScored;
    private Integer pointsConceded;
    private Integer rankingPoints;

    // Final results
    private Integer finalPosition;
    private BigDecimal prizeWon;
    private Boolean prizePaid;

    private LocalDateTime registeredAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentInfo {
        private Long id;
        private String name;
        private String tournamentCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartnerInfo {
        private Long id;
        private String name;
        private String email;
    }
}
