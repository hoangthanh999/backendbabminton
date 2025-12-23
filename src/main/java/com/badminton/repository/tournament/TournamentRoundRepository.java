package com.badminton.repository.tournament;

import com.badminton.entity.tournament.TournamentRound;
import com.badminton.enums.RoundType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRoundRepository extends JpaRepository<TournamentRound, Long> {

    // Basic Queries
    List<TournamentRound> findByTournamentId(Long tournamentId);

    @Query("SELECT tr FROM TournamentRound tr WHERE tr.tournament.id = :tournamentId " +
            "ORDER BY tr.roundNumber")
    List<TournamentRound> findRoundsByTournamentOrdered(@Param("tournamentId") Long tournamentId);

    Optional<TournamentRound> findByTournamentIdAndRoundNumber(Long tournamentId, Integer roundNumber);

    Optional<TournamentRound> findByTournamentIdAndRoundType(Long tournamentId, RoundType roundType);

    // Round Type
    List<TournamentRound> findByRoundType(RoundType roundType);

    // Completion Status
    @Query("SELECT tr FROM TournamentRound tr WHERE tr.tournament.id = :tournamentId " +
            "AND tr.isCompleted = true " +
            "ORDER BY tr.roundNumber")
    List<TournamentRound> findCompletedRounds(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tr FROM TournamentRound tr WHERE tr.tournament.id = :tournamentId " +
            "AND tr.isCompleted = false " +
            "ORDER BY tr.roundNumber")
    List<TournamentRound> findIncompleteRounds(@Param("tournamentId") Long tournamentId);

    // Current Round
    @Query("SELECT tr FROM TournamentRound tr WHERE tr.tournament.id = :tournamentId " +
            "AND tr.isCompleted = false " +
            "ORDER BY tr.roundNumber")
    Optional<TournamentRound> findCurrentRound(@Param("tournamentId") Long tournamentId);

    // Date Queries
    @Query("SELECT tr FROM TournamentRound tr WHERE tr.tournament.id = :tournamentId " +
            "AND tr.startDate <= :date " +
            "AND tr.endDate >= :date")
    Optional<TournamentRound> findRoundByDate(@Param("tournamentId") Long tournamentId,
            @Param("date") LocalDate date);

    @Query("SELECT tr FROM TournamentRound tr WHERE tr.tournament.id = :tournamentId " +
            "AND tr.startDate >= :date " +
            "ORDER BY tr.startDate")
    List<TournamentRound> findUpcomingRounds(@Param("tournamentId") Long tournamentId,
            @Param("date") LocalDate date);

    // Statistics
    @Query("SELECT COUNT(tr) FROM TournamentRound tr WHERE tr.tournament.id = :tournamentId")
    long countRoundsByTournament(@Param("tournamentId") Long tournamentId);

    @Query("SELECT COUNT(tr) FROM TournamentRound tr WHERE tr.tournament.id = :tournamentId " +
            "AND tr.isCompleted = true")
    long countCompletedRounds(@Param("tournamentId") Long tournamentId);
}
