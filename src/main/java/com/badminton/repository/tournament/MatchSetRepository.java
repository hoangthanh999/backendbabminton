package com.badminton.repository.tournament;

import com.badminton.entity.tournament.MatchSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchSetRepository extends JpaRepository<MatchSet, Long> {

    // Basic Queries
    List<MatchSet> findByMatchId(Long matchId);

    @Query("SELECT ms FROM MatchSet ms WHERE ms.match.id = :matchId " +
            "ORDER BY ms.setNumber")
    List<MatchSet> findSetsByMatchOrdered(@Param("matchId") Long matchId);

    Optional<MatchSet> findByMatchIdAndSetNumber(Long matchId, Integer setNumber);

    // Completion Status
    @Query("SELECT ms FROM MatchSet ms WHERE ms.match.id = :matchId " +
            "AND ms.isCompleted = true " +
            "ORDER BY ms.setNumber")
    List<MatchSet> findCompletedSets(@Param("matchId") Long matchId);

    @Query("SELECT ms FROM MatchSet ms WHERE ms.match.id = :matchId " +
            "AND ms.isCompleted = false " +
            "ORDER BY ms.setNumber")
    List<MatchSet> findIncompleteSets(@Param("matchId") Long matchId);

    // Current Set
    @Query("SELECT ms FROM MatchSet ms WHERE ms.match.id = :matchId " +
            "AND ms.isCompleted = false " +
            "ORDER BY ms.setNumber")
    Optional<MatchSet> findCurrentSet(@Param("matchId") Long matchId);

    // Winner Queries
    List<MatchSet> findByWinnerId(Long winnerId);

    @Query("SELECT COUNT(ms) FROM MatchSet ms WHERE ms.match.id = :matchId " +
            "AND ms.winnerId = :participantId")
    long countSetsWonByParticipant(@Param("matchId") Long matchId,
            @Param("participantId") Long participantId);

    // Statistics
    @Query("SELECT COUNT(ms) FROM MatchSet ms WHERE ms.match.id = :matchId")
    long countSetsByMatch(@Param("matchId") Long matchId);

    @Query("SELECT COUNT(ms) FROM MatchSet ms WHERE ms.match.id = :matchId " +
            "AND ms.isCompleted = true")
    long countCompletedSets(@Param("matchId") Long matchId);

    @Query("SELECT AVG(ms.durationMinutes) FROM MatchSet ms WHERE ms.match.id = :matchId " +
            "AND ms.durationMinutes IS NOT NULL")
    Double getAverageSetDuration(@Param("matchId") Long matchId);
}
