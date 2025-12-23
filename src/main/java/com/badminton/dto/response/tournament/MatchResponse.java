package com.badminton.dto.response.tournament;

import com.badminton.enums.MatchStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tournament match response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchResponse {

    private Long id;
    private String matchNumber;

    private TournamentInfo tournament;
    private RoundInfo round;
    private CourtInfo court;

    private ParticipantInfo participant1;
    private ParticipantInfo participant2;

    private MatchStatus status;

    private LocalDateTime scheduledTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;

    private Integer participant1Score;
    private Integer participant2Score;

    private Long winnerId;
    private String winnerName;

    private Boolean isWalkover;
    private String walkoverReason;

    private String bracketPosition;

    private RefereeInfo referee;
    private UmpireInfo umpire;

    private List<SetInfo> sets;

    private Boolean isLive;
    private String streamUrl;
    private String videoUrl;

    private String notes;

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
    public static class RoundInfo {
        private Long id;
        private String roundName;
        private Integer roundNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourtInfo {
        private Long id;
        private String name;
        private String courtNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantInfo {
        private Long id;
        private String name;
        private Integer seedNumber;
        private String teamName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefereeInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UmpireInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetInfo {
        private Integer setNumber;
        private Integer participant1Score;
        private Integer participant2Score;
        private Long winnerId;
        private Boolean isCompleted;
    }
}
