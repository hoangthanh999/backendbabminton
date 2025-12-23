package com.badminton.entity.tournament;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "match_sets", indexes = {
        @Index(name = "idx_match", columnList = "match_id"),
        @Index(name = "idx_set_number", columnList = "set_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchSet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private TournamentMatch match;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(name = "participant1_score", nullable = false)
    @Builder.Default
    private Integer participant1Score = 0;

    @Column(name = "participant2_score", nullable = false)
    @Builder.Default
    private Integer participant2Score = 0;

    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    // Helper Methods

    /**
     * Get winner
     */
    public Long determineWinner() {
        if (participant1Score > participant2Score) {
            return match.getParticipant1().getId();
        } else if (participant2Score > participant1Score) {
            return match.getParticipant2().getId();
        }
        return null;
    }

    /**
     * Complete set
     */
    public void complete() {
        this.isCompleted = true;
        this.winnerId = determineWinner();
    }
}
