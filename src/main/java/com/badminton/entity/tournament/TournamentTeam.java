package com.badminton.entity.tournament;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournament_teams", uniqueConstraints = @UniqueConstraint(name = "uk_tournament_name", columnNames = {
        "tournament_id", "name" }), indexes = {
                @Index(name = "idx_tournament", columnList = "tournament_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentTeam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "logo")
    private String logo;

    @Column(name = "color", length = 7)
    private String color;

    @Column(name = "seed_number")
    private Integer seedNumber;

    @OneToMany(mappedBy = "team")
    @Builder.Default
    private Set<TournamentParticipant> members = new HashSet<>();

    @Column(name = "matches_played")
    @Builder.Default
    private Integer matchesPlayed = 0;

    @Column(name = "matches_won")
    @Builder.Default
    private Integer matchesWon = 0;

    @Column(name = "matches_lost")
    @Builder.Default
    private Integer matchesLost = 0;

    @Column(name = "points")
    @Builder.Default
    private Integer points = 0;

    @Column(name = "ranking")
    private Integer ranking;
}
