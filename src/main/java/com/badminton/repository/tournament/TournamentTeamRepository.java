package com.badminton.repository.tournament;

import com.badminton.entity.tournament.TournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentTeamRepository extends JpaRepository<TournamentTeam, Long> {

    // Basic Queries
    List<TournamentTeam> findByTournamentId(Long tournamentId);

    Optional<TournamentTeam> findByTournamentIdAndName(Long tournamentId, String name);

    boolean existsByTournamentIdAndName(Long tournamentId, String name);

    // Seeding
    @Query("SELECT tt FROM TournamentTeam tt WHERE tt.tournament.id = :tournamentId " +
            "AND tt.seedNumber IS NOT NULL " +
            "ORDER BY tt.seedNumber")
    List<TournamentTeam> findSeededTeams(@Param("tournamentId") Long tournamentId);

    Optional<TournamentTeam> findByTournamentIdAndSeedNumber(Long tournamentId, Integer seedNumber);

    // Standings
    @Query("SELECT tt FROM TournamentTeam tt WHERE tt.tournament.id = :tournamentId " +
            "ORDER BY tt.points DESC, (tt.matchesWon - tt.matchesLost) DESC")
    List<TournamentTeam> findTeamStandings(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tt FROM TournamentTeam tt WHERE tt.tournament.id = :tournamentId " +
            "AND tt.ranking IS NOT NULL " +
            "ORDER BY tt.ranking")
    List<TournamentTeam> findTeamsByRanking(@Param("tournamentId") Long tournamentId);

    // Performance
    @Query("SELECT tt FROM TournamentTeam tt WHERE tt.tournament.id = :tournamentId " +
            "ORDER BY tt.matchesWon DESC")
    List<TournamentTeam> findTeamsByWins(@Param("tournamentId") Long tournamentId);

    // Statistics
    @Query("SELECT COUNT(tt) FROM TournamentTeam tt WHERE tt.tournament.id = :tournamentId")
    long countTeamsByTournament(@Param("tournamentId") Long tournamentId);

    @Query("SELECT COUNT(tm.members) FROM TournamentTeam tm WHERE tm.id = :teamId")
    long countTeamMembers(@Param("teamId") Long teamId);
}
