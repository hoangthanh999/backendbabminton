package com.badminton.repository.tournament;

import com.badminton.entity.tournament.TournamentPrize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentPrizeRepository extends JpaRepository<TournamentPrize, Long> {

    // ==================== BASIC QUERIES ====================

    /**
     * Tìm tất cả giải thưởng trong giải đấu
     */
    List<TournamentPrize> findByTournamentIdOrderByRankAsc(Long tournamentId);

    /**
     * Tìm giải thưởng theo hạng
     */
    Optional<TournamentPrize> findByTournamentIdAndRank(Long tournamentId, Integer rank);

    /**
     * Tìm giải thưởng theo tên
     */
    Optional<TournamentPrize> findByTournamentIdAndPrizeName(Long tournamentId, String prizeName);

    /**
     * Đếm số giải thưởng
     */
    long countByTournamentId(Long tournamentId);

    // ==================== RANK QUERIES ====================

    /**
     * Tìm giải vô địch
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.rank = 1")
    Optional<TournamentPrize> findChampionPrize(@Param("tournamentId") Long tournamentId);

    /**
     * Tìm giải á quân
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.rank = 2")
    Optional<TournamentPrize> findRunnerUpPrize(@Param("tournamentId") Long tournamentId);

    /**
     * Tìm giải hạng ba
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.rank = 3")
    List<TournamentPrize> findThirdPlacePrizes(@Param("tournamentId") Long tournamentId);

    /**
     * Tìm top giải thưởng
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.rank <= :topRank " +
            "ORDER BY tp.rank")
    List<TournamentPrize> findTopPrizes(
            @Param("tournamentId") Long tournamentId,
            @Param("topRank") int topRank);

    // ==================== PRIZE VALUE QUERIES ====================

    /**
     * Tính tổng giá trị giải thưởng tiền mặt
     */
    @Query("SELECT SUM(tp.cashPrize) FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId")
    Optional<BigDecimal> calculateTotalCashPrize(@Param("tournamentId") Long tournamentId);

    /**
     * Tìm giải thưởng có tiền mặt
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.cashPrize > 0 " +
            "ORDER BY tp.cashPrize DESC")
    List<TournamentPrize> findPrizesWithCash(@Param("tournamentId") Long tournamentId);

    /**
     * Tìm giải thưởng có hiện vật
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.physicalPrize IS NOT NULL " +
            "AND tp.physicalPrize != '' " +
            "ORDER BY tp.rank")
    List<TournamentPrize> findPrizesWithPhysicalReward(@Param("tournamentId") Long tournamentId);

    /**
     * Lấy giải thưởng giá trị nhất
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "ORDER BY tp.cashPrize DESC " +
            "LIMIT 1")
    Optional<TournamentPrize> findHighestValuePrize(@Param("tournamentId") Long tournamentId);

    // ==================== WINNER QUERIES ====================

    /**
     * Tìm giải thưởng đã có người nhận
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.winnerTeam IS NOT NULL " +
            "ORDER BY tp.rank")
    List<TournamentPrize> findAwardedPrizes(@Param("tournamentId") Long tournamentId);

    /**
     * Tìm giải thưởng chưa có người nhận
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.winnerTeam IS NULL " +
            "ORDER BY tp.rank")
    List<TournamentPrize> findUnassignedPrizes(@Param("tournamentId") Long tournamentId);

    /**
     * Tìm giải thưởng của đội
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.winnerTeam.id = :teamId")
    List<TournamentPrize> findTeamPrizes(
            @Param("tournamentId") Long tournamentId,
            @Param("teamId") Long teamId);

    /**
     * Kiểm tra đội đã nhận giải chưa
     */
    boolean existsByTournamentIdAndWinnerTeamId(Long tournamentId, Long teamId);

    // ==================== STATISTICS QUERIES ====================

    /**
     * Thống kê giải thưởng
     */
    @Query("SELECT " +
            "COUNT(tp) as totalPrizes, " +
            "COUNT(CASE WHEN tp.winnerTeam IS NOT NULL THEN 1 END) as awarded, " +
            "COUNT(CASE WHEN tp.cashPrize > 0 THEN 1 END) as withCash, " +
            "COUNT(CASE WHEN tp.physicalPrize IS NOT NULL THEN 1 END) as withPhysical, " +
            "SUM(tp.cashPrize) as totalCash " +
            "FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId")
    Object[] getPrizeStatistics(@Param("tournamentId") Long tournamentId);

    /**
     * Phân bố giải thưởng theo hạng
     */
    @Query("SELECT tp.rank, COUNT(tp), SUM(tp.cashPrize) " +
            "FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "GROUP BY tp.rank " +
            "ORDER BY tp.rank")
    List<Object[]> getPrizeDistribution(@Param("tournamentId") Long tournamentId);

    // ==================== VALIDATION QUERIES ====================

    /**
     * Kiểm tra hạng đã tồn tại chưa
     */
    boolean existsByTournamentIdAndRank(Long tournamentId, Integer rank);

    /**
     * Kiểm tra tên giải đã tồn tại chưa
     */
    boolean existsByTournamentIdAndPrizeName(Long tournamentId, String prizeName);

    /**
     * Lấy hạng cao nhất
     */
    @Query("SELECT MAX(tp.rank) FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId")
    Optional<Integer> findMaxRank(@Param("tournamentId") Long tournamentId);

    // ==================== ADVANCED QUERIES ====================

    /**
     * Lấy giải thưởng với thông tin người thắng
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "LEFT JOIN FETCH tp.winnerTeam wt " +
            "LEFT JOIN FETCH wt.captainUser " +
            "WHERE tp.tournament.id = :tournamentId " +
            "ORDER BY tp.rank")
    List<TournamentPrize> findPrizesWithWinnerDetails(@Param("tournamentId") Long tournamentId);

    /**
     * Lấy danh sách đội thắng giải
     */
    @Query("SELECT tp.winnerTeam, tp.prizeName, tp.cashPrize, tp.physicalPrize " +
            "FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND tp.winnerTeam IS NOT NULL " +
            "ORDER BY tp.rank")
    List<Object[]> getWinnersList(@Param("tournamentId") Long tournamentId);

    /**
     * Tìm giải thưởng đặc biệt (không theo hạng)
     */
    @Query("SELECT tp FROM TournamentPrize tp " +
            "WHERE tp.tournament.id = :tournamentId " +
            "AND (tp.prizeName LIKE '%Best%' " +
            "     OR tp.prizeName LIKE '%MVP%' " +
            "     OR tp.prizeName LIKE '%Fair Play%') " +
            "ORDER BY tp.prizeName")
    List<TournamentPrize> findSpecialPrizes(@Param("tournamentId") Long tournamentId);
}
