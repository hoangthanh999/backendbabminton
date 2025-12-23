package com.badminton.dto.request.tournament;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Update match score request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMatchScoreRequest {

    @NotNull(message = "Match ID không được để trống")
    private Long matchId;

    @NotNull(message = "Set scores không được để trống")
    private List<SetScore> setScores;

    private Long winnerId;

    private Boolean isCompleted;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetScore {

        @NotNull(message = "Set number không được để trống")
        @Min(value = 1, message = "Set number phải >= 1")
        private Integer setNumber;

        @NotNull(message = "Điểm participant 1 không được để trống")
        @Min(value = 0, message = "Điểm phải >= 0")
        private Integer participant1Score;

        @NotNull(message = "Điểm participant 2 không được để trống")
        @Min(value = 0, message = "Điểm phải >= 0")
        private Integer participant2Score;

        private Long winnerId;

        private Boolean isCompleted;
    }
}
