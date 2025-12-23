package com.badminton.entity.tournament;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.user.User;
import com.badminton.enums.TournamentFormat;
import com.badminton.enums.TournamentStatus;
import com.badminton.enums.TournamentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournaments", uniqueConstraints = @UniqueConstraint(name = "uk_code", columnNames = "tournament_code"), indexes = {
        @Index(name = "idx_branch_status", columnList = "branch_id, status"),
        @Index(name = "idx_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_type", columnList = "tournament_type"),
        @Index(name = "idx_registration", columnList = "registration_start, registration_end")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE tournaments SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Tournament extends AuditableEntity {

    // Core Info
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "tournament_code", unique = true, nullable = false, length = 50)
    private String tournamentCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_type", nullable = false, length = 30)
    private TournamentType tournamentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_format", nullable = false, length = 30)
    private TournamentFormat tournamentFormat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TournamentStatus status = TournamentStatus.DRAFT;

    // Dates
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "registration_start", nullable = false)
    private LocalDate registrationStart;

    @Column(name = "registration_end", nullable = false)
    private LocalDate registrationEnd;

    // Venue
    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "venue_address", columnDefinition = "TEXT")
    private String venueAddress;

    @Column(name = "venue_city", length = 100)
    private String venueCity;

    @Column(name = "venue_map_url")
    private String venueMapUrl;

    // Capacity
    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "min_participants")
    @Builder.Default
    private Integer minParticipants = 4;

    @Column(name = "current_participants")
    @Builder.Default
    private Integer currentParticipants = 0;

    @Column(name = "max_teams")
    private Integer maxTeams;

    @Column(name = "team_size")
    @Builder.Default
    private Integer teamSize = 1; // 1 for singles, 2 for doubles

    // Financial
    @Column(name = "entry_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal entryFee = BigDecimal.ZERO;

    @Column(name = "prize_pool", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal prizePool = BigDecimal.ZERO;

    @Column(name = "first_prize", precision = 10, scale = 2)
    private BigDecimal firstPrize;

    @Column(name = "second_prize", precision = 10, scale = 2)
    private BigDecimal secondPrize;

    @Column(name = "third_prize", precision = 10, scale = 2)
    private BigDecimal thirdPrize;

    @Column(name = "prize_distribution", columnDefinition = "JSON")
    private String prizeDistribution; // JSON: {"1st": 50%, "2nd": 30%, "3rd": 20%}

    @Column(name = "total_collected", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalCollected = BigDecimal.ZERO;

    @Column(name = "total_paid_out", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalPaidOut = BigDecimal.ZERO;

    // Rules & Requirements
    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;

    @Column(name = "eligibility_criteria", columnDefinition = "TEXT")
    private String eligibilityCriteria;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "skill_level", length = 50)
    private String skillLevel; // BEGINNER, INTERMEDIATE, ADVANCED, PROFESSIONAL

    @Column(name = "gender_restriction", length = 20)
    private String genderRestriction; // MALE, FEMALE, MIXED, ANY

    // Organizer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @Column(name = "organizer_name")
    private String organizerName;

    @Column(name = "organizer_contact")
    private String organizerContact;

    @Column(name = "organizer_email")
    private String organizerEmail;

    // Sponsors
    @Column(name = "sponsors", columnDefinition = "JSON")
    private String sponsors; // JSON array

    @Column(name = "sponsor_logos", columnDefinition = "JSON")
    private String sponsorLogos; // JSON array

    // Media
    @Column(name = "banner_image")
    private String bannerImage;

    @Column(name = "poster_image")
    private String posterImage;

    @Column(name = "logo")
    private String logo;

    @Column(name = "gallery", columnDefinition = "JSON")
    private String gallery; // JSON array

    // Live Streaming
    @Column(name = "is_live_streamed")
    @Builder.Default
    private Boolean isLiveStreamed = false;

    @Column(name = "stream_url")
    private String streamUrl;

    @Column(name = "stream_platform", length = 50)
    private String streamPlatform; // YOUTUBE, FACEBOOK, TWITCH

    // Settings
    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "allow_spectators")
    @Builder.Default
    private Boolean allowSpectators = true;

    @Column(name = "spectator_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal spectatorFee = BigDecimal.ZERO;

    @Column(name = "require_approval")
    @Builder.Default
    private Boolean requireApproval = false;

    @Column(name = "auto_generate_schedule")
    @Builder.Default
    private Boolean autoGenerateSchedule = true;

    // Statistics
    @Column(name = "total_matches")
    @Builder.Default
    private Integer totalMatches = 0;

    @Column(name = "completed_matches")
    @Builder.Default
    private Integer completedMatches = 0;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    // Additional Info
    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Calculated Fields
    @Formula("(SELECT COUNT(*) FROM tournament_participants tp WHERE tp.tournament_id = id AND tp.status = 'CONFIRMED')")
    private Integer confirmedParticipants;

    @Formula("(SELECT COUNT(*) FROM tournament_matches tm WHERE tm.tournament_id = id)")
    private Integer matchCount;

    // Relationships
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TournamentParticipant> participants = new HashSet<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TournamentMatch> matches = new HashSet<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TournamentRound> rounds = new HashSet<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TournamentPrize> prizes = new HashSet<>();

    // Lifecycle
    @PrePersist
    public void prePersist() {
        if (tournamentCode == null) {
            tournamentCode = generateTournamentCode();
        }

        if (slug == null && name != null) {
            slug = generateSlug(name);
        }
    }

    // Helper Methods

    /**
     * Generate tournament code
     */
    private String generateTournamentCode() {
        String typePrefix = tournamentType != null ? tournamentType.name().substring(0, 3).toUpperCase() : "TRN";

        return String.format("%s%s%04d",
                typePrefix,
                LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyMMdd")),
                (int) (Math.random() * 10000));
    }

    /**
     * Generate slug
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    /**
     * Check if registration is open
     */
    public boolean isRegistrationOpen() {
        LocalDate now = LocalDate.now();
        return status == TournamentStatus.OPEN &&
                !now.isBefore(registrationStart) &&
                !now.isAfter(registrationEnd) &&
                (maxParticipants == null || currentParticipants < maxParticipants);
    }

    /**
     * Check if tournament is full
     */
    public boolean isFull() {
        return maxParticipants != null && currentParticipants >= maxParticipants;
    }

    /**
     * Check if tournament can start
     */
    public boolean canStart() {
        return status == TournamentStatus.OPEN &&
                currentParticipants >= minParticipants &&
                !LocalDate.now().isBefore(startDate);
    }

    /**
     * Start tournament
     */
    public void start() {
        if (!canStart()) {
            throw new IllegalStateException("Tournament cannot be started");
        }

        this.status = TournamentStatus.IN_PROGRESS;
    }

    /**
     * Complete tournament
     */
    public void complete() {
        if (status != TournamentStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress tournaments can be completed");
        }

        this.status = TournamentStatus.COMPLETED;
    }

    /**
     * Cancel tournament
     */
    public void cancel(String reason) {
        if (status == TournamentStatus.COMPLETED) {
            throw new IllegalStateException("Completed tournaments cannot be cancelled");
        }

        this.status = TournamentStatus.CANCELLED;
        this.notes = (notes != null ? notes + "\n" : "") + "Cancelled: " + reason;
    }

    /**
     * Add participant
     */
    public void addParticipant(TournamentParticipant participant) {
        if (isFull()) {
            throw new IllegalStateException("Tournament is full");
        }

        participants.add(participant);
        participant.setTournament(this);
        this.currentParticipants++;

        // Update total collected
        if (entryFee != null) {
            this.totalCollected = this.totalCollected.add(entryFee);
        }
    }

    /**
     * Remove participant
     */
    public void removeParticipant(TournamentParticipant participant) {
        participants.remove(participant);
        participant.setTournament(null);
        this.currentParticipants--;

        // Update total collected
        if (entryFee != null) {
            this.totalCollected = this.totalCollected.subtract(entryFee);
        }
    }

    /**
     * Calculate completion percentage
     */
    public BigDecimal getCompletionPercentage() {
        if (totalMatches == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(completedMatches)
                .divide(BigDecimal.valueOf(totalMatches), 2, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Get duration in days
     */
    public Long getDurationInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Get days until start
     */
    public Long getDaysUntilStart() {
        if (LocalDate.now().isAfter(startDate)) {
            return 0L;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), startDate);
    }

    /**
     * Get days until registration closes
     */
    public Long getDaysUntilRegistrationClose() {
        if (LocalDate.now().isAfter(registrationEnd)) {
            return 0L;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), registrationEnd);
    }

    /**
     * Check if is ongoing
     */
    public boolean isOngoing() {
        LocalDate now = LocalDate.now();
        return status == TournamentStatus.IN_PROGRESS &&
                !now.isBefore(startDate) &&
                !now.isAfter(endDate);
    }

    /**
     * Calculate remaining prize pool
     */
    public BigDecimal getRemainingPrizePool() {
        return prizePool.subtract(totalPaidOut);
    }

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount++;
    }
}
