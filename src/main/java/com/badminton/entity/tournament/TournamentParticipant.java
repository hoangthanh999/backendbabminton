package com.badminton.entity.tournament;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.ParticipantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_participants", uniqueConstraints = @UniqueConstraint(name = "uk_tournament_user", columnNames = {
        "tournament_id", "user_id" }), indexes = {
                @Index(name = "idx_tournament", columnList = "tournament_id"),
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_team", columnList = "team_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentParticipant extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TournamentTeam team;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.PENDING;

    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    @Column(name = "seed_number")
    private Integer seedNumber; // Seeding for tournament bracket

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(name = "player_email")
    private String playerEmail;

    @Column(name = "player_phone", length = 20)
    private String playerPhone;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    // Partner Info (for doubles)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private User partner;

    @Column(name = "partner_name")
    private String partnerName;

    // Registration
    @Column(name = "registered_at")
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @Column(name = "is_checked_in")
    @Builder.Default
    private Boolean isCheckedIn = false;

    // Payment
    @Column(name = "entry_fee_paid")
    @Builder.Default
    private Boolean entryFeePaid = false;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // Performance
    @Column(name = "matches_played")
    @Builder.Default
    private Integer matchesPlayed = 0;

    @Column(name = "matches_won")
    @Builder.Default
    private Integer matchesWon = 0;

    @Column(name = "matches_lost")
    @Builder.Default
    private Integer matchesLost = 0;

    @Column(name = "sets_won")
    @Builder.Default
    private Integer setsWon = 0;

    @Column(name = "sets_lost")
    @Builder.Default
    private Integer setsLost = 0;

    @Column(name = "points_scored")
    @Builder.Default
    private Integer pointsScored = 0;

    @Column(name = "points_conceded")
    @Builder.Default
    private Integer pointsConceded = 0;

    @Column(name = "ranking_points")
    @Builder.Default
    private Integer rankingPoints = 0;

    @Column(name = "final_position")
    private Integer finalPosition;

    // Prize
    @Column(name = "prize_won", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal prizeWon = BigDecimal.ZERO;

    @Column(name = "prize_paid")
    @Builder.Default
    private Boolean prizePaid = false;

    @Column(name = "prize_paid_at")
    private LocalDateTime prizePaidAt;

    // Additional Info
    @Column(name = "skill_level", length = 50)
    private String skillLevel;

    @Column(name = "previous_tournaments")
    private Integer previousTournaments;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "medical_info", columnDefinition = "TEXT")
    private String medicalInfo;

    @Column(name = "jersey_size", length = 10)
    private String jerseySize;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    // Approval
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Lifecycle
    @PrePersist
    public void prePersist() {
        if (registrationNumber == null) {
            registrationNumber = generateRegistrationNumber();
        }

        if (playerName == null && user != null) {
            playerName = user.getName();
            playerEmail = user.getEmail();
            playerPhone = user.getPhone();
        }
    }

    // Helper Methods

    /**
     * Generate registration number
     */
    private String generateRegistrationNumber() {
        return String.format("REG%s%06d",
                LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                (int) (Math.random() * 1000000));
    }

    /**
     * Confirm participation
     */
    public void confirm(User approver) {
        if (status != ParticipantStatus.PENDING) {
            throw new IllegalStateException("Only pending participants can be confirmed");
        }

        this.status = ParticipantStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * Reject participation
     */
    public void reject(String reason) {
        if (status != ParticipantStatus.PENDING) {
            throw new IllegalStateException("Only pending participants can be rejected");
        }

        this.status = ParticipantStatus.REJECTED;
        this.rejectionReason = reason;
    }

    /**
     * Check in participant
     */
    public void checkIn() {
        if (status != ParticipantStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed participants can check in");
        }

        this.isCheckedIn = true;
        this.checkedInAt = LocalDateTime.now();
    }

    /**
     * Record match result
     */
    public void recordMatchResult(boolean won, int setsWon, int setsLost,
            int pointsScored, int pointsConceded) {
        this.matchesPlayed++;

        if (won) {
            this.matchesWon++;
        } else {
            this.matchesLost++;
        }

        this.setsWon += setsWon;
        this.setsLost += setsLost;
        this.pointsScored += pointsScored;
        this.pointsConceded += pointsConceded;
    }

    /**
     * Award prize
     */
    public void awardPrize(BigDecimal amount) {
        this.prizeWon = amount;
    }

    /**
     * Mark prize as paid
     */
    public void markPrizePaid() {
        this.prizePaid = true;
        this.prizePaidAt = LocalDateTime.now();
    }

    /**
     * Calculate win rate
     */
    public BigDecimal getWinRate() {
        if (matchesPlayed == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(matchesWon)
                .divide(BigDecimal.valueOf(matchesPlayed), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Calculate point differential
     */
    public Integer getPointDifferential() {
        return pointsScored - pointsConceded;
    }

    /**
     * Get display name (with partner for doubles)
     */
    public String getDisplayName() {
        if (partner != null || partnerName != null) {
            String partnerDisplay = partnerName != null ? partnerName : partner.getName();
            return playerName + " / " + partnerDisplay;
        }
        return playerName;
    }
}
