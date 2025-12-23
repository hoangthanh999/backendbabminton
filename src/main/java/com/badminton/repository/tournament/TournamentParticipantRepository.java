package com.badminton.repository.tournament;

import com.badminton.entity.tournament.TournamentParticipant;
import com.badminton.enums.ParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {

    // Basic Queries
    Optional<TournamentParticipant> findByRegistrationNumber(String registrationNumber);

    List<TournamentParticipant> findByTournamentId(Long tournamentId);

    Page<TournamentParticipant> findByTournamentId(Long tournamentId, Pageable pageable);

    List<TournamentParticipant> findByUserId(Long userId);

    Page<TournamentParticipant> findByUserId(Long userId, Pageable pageable);

    Optional<TournamentParticipant> findByTournamentIdAndUserId(Long tournamentId, Long userId);

    boolean existsByTournamentIdAndUserId(Long tournamentId, Long userId);

    // Status Queries
    List<TournamentParticipant> findByStatus(ParticipantStatus status);

    List<TournamentParticipant> findByTournamentIdAndStatus(Long tournamentId, ParticipantStatus status);

    Page<TournamentParticipant> findByTournamentIdAndStatus(Long tournamentId,
            ParticipantStatus status,
            Pageable pageable);

    List<TournamentParticipant> findByUserIdAndStatus(Long userId, ParticipantStatus status);

    // Confirmed Participants
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.status = 'CONFIRMED' " +
            "ORDER BY tp.seedNumber")
    List<TournamentParticipant> findConfirmedParticipantsByTournament(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.status = 'CONFIRMED' " +
            "ORDER BY tp.registeredAt")
    Page<TournamentParticipant> findConfirmedParticipantsByTournament(@Param("tournamentId") Long tournamentId,
            Pageable pageable);

    // Pending Participants
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.status = 'PENDING' " +
            "ORDER BY tp.registeredAt")
    List<TournamentParticipant> findPendingParticipantsByTournament(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.status = 'PENDING' " +
            "ORDER BY tp.registeredAt")
    List<TournamentParticipant> findAllPendingParticipants();

    // Check-in Status
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.isCheckedIn = true")
    List<TournamentParticipant> findCheckedInParticipants(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.status = 'CONFIRMED' " +
            "AND tp.isCheckedIn = false")
    List<TournamentParticipant> findNotCheckedInParticipants(@Param("tournamentId") Long tournamentId);

    // Payment Status
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.entryFeePaid = false " +
            "AND tp.status = 'CONFIRMED'")
    List<TournamentParticipant> findUnpaidParticipants(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.entryFeePaid = true")
    List<TournamentParticipant> findPaidParticipants(@Param("tournamentId") Long tournamentId);

    // Team Queries
    List<TournamentParticipant> findByTeamId(Long teamId);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.team IS NOT NULL " +
            "ORDER BY tp.team.id")
    List<TournamentParticipant> findTeamParticipants(@Param("tournamentId") Long tournamentId);

    // Partner Queries (for doubles)
    List<TournamentParticipant> findByPartnerId(Long partnerId);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.partner IS NOT NULL")
    List<TournamentParticipant> findDoublesParticipants(@Param("tournamentId") Long tournamentId);

    // Seeding
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.seedNumber IS NOT NULL " +
            "ORDER BY tp.seedNumber")
    List<TournamentParticipant> findSeededParticipants(@Param("tournamentId") Long tournamentId);

    Optional<TournamentParticipant> findByTournamentIdAndSeedNumber(Long tournamentId, Integer seedNumber);

    // Performance Queries
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.status = 'CONFIRMED' " +
            "ORDER BY tp.matchesWon DESC, tp.pointsScored DESC")
    List<TournamentParticipant> findParticipantsByPerformance(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.finalPosition IS NOT NULL " +
            "ORDER BY tp.finalPosition")
    List<TournamentParticipant> findParticipantsByFinalPosition(@Param("tournamentId") Long tournamentId);

    // Winners & Podium
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.finalPosition <= 3 " +
            "ORDER BY tp.finalPosition")
    List<TournamentParticipant> findPodiumFinishers(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.finalPosition = 1")
    Optional<TournamentParticipant> findWinner(@Param("tournamentId") Long tournamentId);

    // Prize Queries
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.prizeWon > 0 " +
            "ORDER BY tp.prizeWon DESC")
    List<TournamentParticipant> findPrizeWinners(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.prizeWon > 0 " +
            "AND tp.prizePaid = false")
    List<TournamentParticipant> findUnpaidPrizes();

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.prizeWon > 0 " +
            "AND tp.prizePaid = false")
    List<TournamentParticipant> findUnpaidPrizesByTournament(@Param("tournamentId") Long tournamentId);

    // Statistics
    @Query("SELECT COUNT(tp) FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.status = :status")
    long countByTournamentAndStatus(@Param("tournamentId") Long tournamentId,
            @Param("status") ParticipantStatus status);

    @Query("SELECT COUNT(tp) FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.status = 'CONFIRMED'")
    long countConfirmedParticipants(@Param("tournamentId") Long tournamentId);

    @Query("SELECT COUNT(tp) FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.isCheckedIn = true")
    long countCheckedInParticipants(@Param("tournamentId") Long tournamentId);

    @Query("SELECT SUM(tp.prizeWon) FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId")
    BigDecimal sumTotalPrizesAwarded(@Param("tournamentId") Long tournamentId);

    @Query("SELECT SUM(tp.prizeWon) FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.prizePaid = true")
    BigDecimal sumPrizesPaid(@Param("tournamentId") Long tournamentId);

    // User Tournament History
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.user.id = :userId " +
            "AND tp.status = 'CONFIRMED' " +
            "ORDER BY tp.tournament.startDate DESC")
    Page<TournamentParticipant> findUserTournamentHistory(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(tp) FROM TournamentParticipant tp WHERE tp.user.id = :userId " +
            "AND tp.status = 'CONFIRMED'")
    long countUserTournaments(@Param("userId") Long userId);

    @Query("SELECT COUNT(tp) FROM TournamentParticipant tp WHERE tp.user.id = :userId " +
            "AND tp.finalPosition = 1")
    long countUserWins(@Param("userId") Long userId);

    @Query("SELECT SUM(tp.prizeWon) FROM TournamentParticipant tp WHERE tp.user.id = :userId")
    BigDecimal sumUserTotalPrizes(@Param("userId") Long userId);

    // Registration Date Range
    List<TournamentParticipant> findByRegisteredAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND tp.registeredAt BETWEEN :startDate AND :endDate " +
            "ORDER BY tp.registeredAt")
    List<TournamentParticipant> findByTournamentAndRegistrationDateRange(
            @Param("tournamentId") Long tournamentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Search
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.id = :tournamentId " +
            "AND (LOWER(tp.playerName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(tp.playerEmail) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR tp.playerPhone LIKE CONCAT('%', :keyword, '%') " +
            "OR tp.registrationNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<TournamentParticipant> searchParticipants(@Param("tournamentId") Long tournamentId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Top Performers
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament.branch.id = :branchId " +
            "AND tp.status = 'CONFIRMED' " +
            "ORDER BY tp.rankingPoints DESC")
    List<TournamentParticipant> findTopPerformersByBranch(@Param("branchId") Long branchId, Pageable pageable);
}
