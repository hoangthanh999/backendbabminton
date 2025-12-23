package com.badminton.dto.request.tournament;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Create tournament match request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMatchRequest {

    @NotNull(message = "Tournament ID không được để trống")
    private Long tournamentId;

    @NotNull(message = "Round ID không được để trống")
    private Long roundId;

    @NotNull(message = "Participant 1 không được để trống")
    private Long participant1Id;

    @NotNull(message = "Participant 2 không được để trống")
    private Long participant2Id;

    @NotNull(message = "Court ID không được để trống")
    private Long courtId;

    @NotNull(message = "Thời gian thi đấu không được để trống")
    @Future(message = "Thời gian thi đấu phải là thời gian trong tương lai")
    private LocalDateTime scheduledTime;

    private Integer matchNumber;

    private String bracketPosition;

    private Long nextMatchId;

    private Long refereeId;

    private Long umpireId;

    private String notes;
}
