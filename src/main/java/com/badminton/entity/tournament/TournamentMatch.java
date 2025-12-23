package com.badminton.entity.tournament;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.court.Court;
import com.badminton.entity.user.User;
import com.badminton.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournament_matches", indexes = {
        @Index(name = "idx_tournament", columnList = "tournament_id"),
        @Index(name = "idx_round", columnList = "round_id"),
        @Index(name = "idx_court", columnList = "court_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_scheduled", columnList = "scheduled_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentMatch extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private TournamentRound round;

    @Column(name = "match_number")
    private Integer matchNumber;

    @Column(name = "bracket_position")
    private String bracketPosition; // e.g., "QF1", "SF1", "F"

    // Participants
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant1_id", nullable = false)
    private TournamentParticipant participant1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant2_id", nullable = false)
    private TournamentParticipant participant2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private TournamentParticipant winner;

    // Scheduling
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MatchStatus status = MatchStatus.SCHEDULED;

    // Score
    @Column(name = "participant1_score")
    @Builder.Default
    private Integer participant1Score = 0;

    @Column(name = "participant2_score")
    @Builder.Default
    private Integer participant2Score = 0;

    @Column(name = "best_of_sets")
    @Builder.Default
    private Integer bestOfSets = 3; // Best of 3 or 5

    @Column(name = "score_details", columnDefinition = "JSON")
    private String scoreDetails; // JSON: [{"set": 1, "p1": 21, "p2": 19}, ...]

    // Officials
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referee_id")
    private User referee;

    @Column(name = "referee_name")
    private String refereeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "umpire_id")
    private User umpire;

    @Column(name = "umpire_name")
    private String umpireName;

    // Additional Info
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_walkover")
    @Builder.Default
    private Boolean isWalkover = false;

    @Column(name = "walkover_reason", columnDefinition = "TEXT")
    private String walkoverReason;

    @Column(name = "is_live")
    @Builder.Default
    private Boolean isLive = false;

    @Column(name = "stream_url")
    private String streamUrl;

    @Column(name = "video_url")
    private String videoUrl;

    // Next Match (for bracket progression)
    @Column(name = "next_match_id")
    private Long nextMatchId;

    @Column(name = "next_match_position")
    private String nextMatchPosition; // "winner" or "loser"

    // Relationships
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MatchSet> sets = new HashSet<>();

    // Helper Methods

    /**
     * Start match
     */
    public void start() {
        if (status != MatchStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled matches can be started");
        }

        this.status = MatchStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
        this.isLive = true;
    }

    /**
     * Complete match
     */
    public void complete(TournamentParticipant winner) {
        if (status != MatchStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress matches can be completed");
        }

        this.status = MatchStatus.COMPLETED;
        this.winner = winner;
        this.actualEndTime = LocalDateTime.now();
        this.isLive = false;

        // Update participant statistics
        participant1.recordMatchResult(
                winner.equals(participant1),
                participant1Score,
                participant2Score,
                0, 0 // Points would be calculated from sets
        );

        participant2.recordMatchResult(
                winner.equals(participant2),
                participant2Score,
                participant1Score,
                0, 0);
    }

    /**
     * Cancel match
     */
    public void cancel(String reason) {
        if (status == MatchStatus.COMPLETED) {
            throw new IllegalStateException("Completed matches cannot be cancelled");
        }

        this.status = MatchStatus.CANCELLED;
        this.notes = (notes != null ? notes + "\n" : "") + "Cancelled: " + reason;
    }

    /**
     * Record walkover
     */
    public void recordWalkover(TournamentParticipant winner, String reason) {
        this.isWalkover = true;
        this.walkoverReason = reason;
        this.winner = winner;
        this.status = MatchStatus.COMPLETED;

        // Walkover scores
        if (winner.equals(participant1)) {
            this.participant1Score = bestOfSets;
            this.participant2Score = 0;
        } else {
            this.participant1Score = 0;
            this.participant2Score = bestOfSets;
        }
    }

    /**
     * Get match duration in minutes
     */
    public Long getDurationInMinutes() {
        if (actualStartTime == null || actualEndTime == null) {
            return null;
        }

        return java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
    }

    /**
     * Check if match is overdue
     */
    public boolean isOverdue() {
        return status == MatchStatus.SCHEDULED &&
                scheduledTime != null &&
                LocalDateTime.now().isAfter(scheduledTime.plusMinutes(30));
    }

    /**
     * Get match title
     */
    public String getMatchTitle() {
        return participant1.getPlayerName() + " vs " + participant2.getPlayerName();
    }
}
