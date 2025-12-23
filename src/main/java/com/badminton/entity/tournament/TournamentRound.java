package com.badminton.entity.tournament;

import com.badminton.entity.base.BaseEntity;
import com.badminton.enums.RoundType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournament_rounds", indexes = {
        @Index(name = "idx_tournament", columnList = "tournament_id"),
        @Index(name = "idx_round_number", columnList = "round_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentRound extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "name", nullable = false)
    private String name; // Round of 16, Quarter-finals, Semi-finals, Finals

    @Enumerated(EnumType.STRING)
    @Column(name = "round_type", length = 30)
    private RoundType roundType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_matches")
    @Builder.Default
    private Integer totalMatches = 0;

    @Column(name = "completed_matches")
    @Builder.Default
    private Integer completedMatches = 0;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @OneToMany(mappedBy = "round")
    @Builder.Default
    private Set<TournamentMatch> matches = new HashSet<>();

    // Helper Methods

    /**
     * Check if round is completed
     */
    public boolean checkIfCompleted() {
        return completedMatches.equals(totalMatches);
    }

    /**
     * Get completion percentage
     */
    public java.math.BigDecimal getCompletionPercentage() {
        if (totalMatches == 0) {
            return java.math.BigDecimal.ZERO;
        }

        return java.math.BigDecimal.valueOf(completedMatches)
                .divide(java.math.BigDecimal.valueOf(totalMatches), 2, java.math.RoundingMode.HALF_UP)
                .multiply(new java.math.BigDecimal("100"));
    }
}
