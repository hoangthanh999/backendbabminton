package com.badminton.repository.tournament;

import com.badminton.entity.tournament.TournamentMatch;
import com.badminton.enums.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long> {

    // Basic Queries
    List<TournamentMatch> findByTournamentId(Long tournamentId);

    Page<TournamentMatch> findByTournamentId(Long tournamentId, Pageable pageable);

    List<TournamentMatch> findByRoundId(Long roundId);

    List<TournamentMatch> findByCourtId(Long courtId);

    // Status Queries
    List<TournamentMatch> findByStatus(MatchStatus status);

    List<TournamentMatch> findByTournamentIdAndStatus(Long tournamentId, MatchStatus status);

    Page<TournamentMatch> findByTournamentIdAndStatus(Long tournamentId,
            MatchStatus status,
            Pageable pageable);

    // Scheduled Matches
    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = 'SCHEDULED' " +
            "ORDER BY tm.scheduledTime")
    List<TournamentMatch> findScheduledMatchesByTournament(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = 'SCHEDULED' " +
            "AND tm.scheduledTime BETWEEN :startTime AND :endTime " +
            "ORDER BY tm.scheduledTime")
    List<TournamentMatch> findScheduledMatchesByTimeRange(@Param("tournamentId") Long tournamentId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // In Progress Matches
    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = 'IN_PROGRESS' " +
            "AND tm.isLive = true")
    List<TournamentMatch> findLiveMatches(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.status = 'IN_PROGRESS' " +
            "AND tm.isLive = true")
    List<TournamentMatch> findAllLiveMatches();

    // Completed Matches
    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = 'COMPLETED' " +
            "ORDER BY tm.actualEndTime DESC")
    List<TournamentMatch> findCompletedMatchesByTournament(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = 'COMPLETED' " +
            "ORDER BY tm.actualEndTime DESC")
    Page<TournamentMatch> findCompletedMatchesByTournament(@Param("tournamentId") Long tournamentId,
            Pageable pageable);

    // Participant Matches
    @Query("SELECT tm FROM TournamentMatch tm WHERE " +
            "(tm.participant1.id = :participantId OR tm.participant2.id = :participantId) " +
            "ORDER BY tm.scheduledTime DESC")
    List<TournamentMatch> findMatchesByParticipant(@Param("participantId") Long participantId);

    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND (tm.participant1.id = :participantId OR tm.participant2.id = :participantId) " +
            "ORDER BY tm.scheduledTime")
    List<TournamentMatch> findMatchesByTournamentAndParticipant(@Param("tournamentId") Long tournamentId,
            @Param("participantId") Long participantId);

    @Query("SELECT tm FROM TournamentMatch tm WHERE " +
            "(tm.participant1.id = :participantId OR tm.participant2.id = :participantId) " +
            "AND tm.status = 'SCHEDULED' " +
            "ORDER BY tm.scheduledTime")
    List<TournamentMatch> findUpcomingMatchesByParticipant(@Param("participantId") Long participantId);

    // Court Schedule
    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.court.id = :courtId " +
            "AND tm.scheduledTime BETWEEN :startTime AND :endTime " +
            "AND tm.status != 'CANCELLED' " +
            "ORDER BY tm.scheduledTime")
    List<TournamentMatch> findMatchesByCourtAndTimeRange(@Param("courtId") Long courtId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT CASE WHEN COUNT(tm) > 0 THEN true ELSE false END FROM TournamentMatch tm " +
            "WHERE tm.court.id = :courtId " +
            "AND tm.scheduledTime < :endTime " +
            "AND DATE_ADD(tm.scheduledTime, INTERVAL 2 HOUR) > :startTime " +
            "AND tm.status NOT IN ('CANCELLED', 'COMPLETED')")
    boolean existsConflictingMatch(@Param("courtId") Long courtId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // Round Matches
    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.round.id = :roundId " +
            "ORDER BY tm.matchNumber")
    List<TournamentMatch> findMatchesByRound(@Param("roundId") Long roundId);

    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.round.id = :roundId " +
            "AND tm.status = 'COMPLETED'")
    List<TournamentMatch> findCompletedMatchesByRound(@Param("roundId") Long roundId);

    // Bracket Position
    Optional<TournamentMatch> findByTournamentIdAndBracketPosition(Long tournamentId, String bracketPosition);

    List<TournamentMatch> findByNextMatchId(Long nextMatchId);

    // Walkover Matches
    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.isWalkover = true")
    List<TournamentMatch> findWalkoverMatches(@Param("tournamentId") Long tournamentId);

    // Overdue Matches
    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.status = 'SCHEDULED' " +
            "AND tm.scheduledTime < :dateTime")
    List<TournamentMatch> findOverdueMatches(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = 'SCHEDULED' " +
            "AND tm.scheduledTime < :dateTime")
    List<TournamentMatch> findOverdueMatchesByTournament(@Param("tournamentId") Long tournamentId,
            @Param("dateTime") LocalDateTime dateTime);

    // Statistics
    @Query("SELECT COUNT(tm) FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = :status")
    long countByTournamentAndStatus(@Param("tournamentId") Long tournamentId,
            @Param("status") MatchStatus status);

    @Query("SELECT COUNT(tm) FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = 'COMPLETED'")
    long countCompletedMatches(@Param("tournamentId") Long tournamentId);

    @Query("SELECT COUNT(tm) FROM TournamentMatch tm WHERE tm.round.id = :roundId " +
            "AND tm.status = 'COMPLETED'")
    long countCompletedMatchesByRound(@Param("roundId") Long roundId);

    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, tm.actualStartTime, tm.actualEndTime)) " +
            "FROM TournamentMatch tm " +
            "WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = 'COMPLETED' " +
            "AND tm.actualStartTime IS NOT NULL " +
            "AND tm.actualEndTime IS NOT NULL")
    Double getAverageMatchDuration(@Param("tournamentId") Long tournamentId);

    // Today's Matches
    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND DATE(tm.scheduledTime) = CURRENT_DATE " +
            "AND tm.status != 'CANCELLED' " +
            "ORDER BY tm.scheduledTime")
    List<TournamentMatch> findTodayMatches(@Param("tournamentId") Long tournamentId);

    // Referee/Umpire Queries
    List<TournamentMatch> findByRefereeId(Long refereeId);

    List<TournamentMatch> findByUmpireId(Long umpireId);

    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.status = 'SCHEDULED' " +
            "AND tm.referee IS NULL")
    List<TournamentMatch> findMatchesWithoutReferee(@Param("tournamentId") Long tournamentId);

    // Video/Stream
    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId " +
            "AND tm.streamUrl IS NOT NULL")
    List<TournamentMatch> findStreamedMatches(@Param("tournamentId") Long tournamentId);
}
