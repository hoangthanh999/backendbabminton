package com.badminton.entity.tournament;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_prizes", indexes = {
        @Index(name = "idx_tournament", columnList = "tournament_id"),
        @Index(name = "idx_participant", columnList = "participant_id"),
        @Index(name = "idx_position", columnList = "position")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentPrize extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private TournamentParticipant participant;

    @Column(name = "position", nullable = false)
    private Integer position; // 1st, 2nd, 3rd, etc.

    @Column(name = "prize_name")
    private String prizeName; // Champion, Runner-up, 3rd Place

    @Column(name = "cash_prize", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cashPrize = BigDecimal.ZERO;

    @Column(name = "trophy_description")
    private String trophyDescription;

    @Column(name = "certificate")
    private String certificate;

    @Column(name = "other_prizes", columnDefinition = "TEXT")
    private String otherPrizes;

    @Column(name = "is_awarded")
    @Builder.Default
    private Boolean isAwarded = false;

    @Column(name = "awarded_at")
    private LocalDateTime awardedAt;

    @Column(name = "is_paid")
    @Builder.Default
    private Boolean isPaid = false;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
