package com.badminton.repository.tournament;

import com.badminton.entity.tournament.Tournament;
import com.badminton.enums.TournamentFormat;
import com.badminton.enums.TournamentStatus;
import com.badminton.enums.TournamentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long>, JpaSpecificationExecutor<Tournament> {

    // Basic Queries
    Optional<Tournament> findByTournamentCode(String tournamentCode);

    Optional<Tournament> findBySlug(String slug);

    Optional<Tournament> findByIdAndDeletedAtIsNull(Long id);

    Optional<Tournament> findBySlugAndDeletedAtIsNull(String slug);

    boolean existsByTournamentCode(String tournamentCode);

    boolean existsBySlug(String slug);

    // Branch Queries
    List<Tournament> findByBranchId(Long branchId);

    Page<Tournament> findByBranchId(Long branchId, Pageable pageable);

    List<Tournament> findByBranchIdAndDeletedAtIsNull(Long branchId);

    Page<Tournament> findByBranchIdAndDeletedAtIsNull(Long branchId, Pageable pageable);

    // Status Queries
    List<Tournament> findByStatus(TournamentStatus status);

    Page<Tournament> findByStatus(TournamentStatus status, Pageable pageable);

    List<Tournament> findByStatusAndDeletedAtIsNull(TournamentStatus status);

    List<Tournament> findByBranchIdAndStatus(Long branchId, TournamentStatus status);

    List<Tournament> findByBranchIdAndStatusAndDeletedAtIsNull(Long branchId, TournamentStatus status);

    // Type & Format Queries
    List<Tournament> findByTournamentType(TournamentType type);

    Page<Tournament> findByTournamentType(TournamentType type, Pageable pageable);

    List<Tournament> findByTournamentFormat(TournamentFormat format);

    List<Tournament> findByBranchIdAndTournamentType(Long branchId, TournamentType type);

    // Active Tournaments
    @Query("SELECT t FROM Tournament t WHERE t.status IN ('OPEN', 'IN_PROGRESS') " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    List<Tournament> findActiveTournaments();

    @Query("SELECT t FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.status IN ('OPEN', 'IN_PROGRESS') " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    List<Tournament> findActiveTournamentsByBranch(@Param("branchId") Long branchId);

    // Open for Registration
    @Query("SELECT t FROM Tournament t WHERE t.status = 'OPEN' " +
            "AND t.registrationStart <= :date " +
            "AND t.registrationEnd >= :date " +
            "AND (t.maxParticipants IS NULL OR t.currentParticipants < t.maxParticipants) " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.registrationEnd")
    List<Tournament> findOpenForRegistration(@Param("date") LocalDate date);

    @Query("SELECT t FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.status = 'OPEN' " +
            "AND t.registrationStart <= :date " +
            "AND t.registrationEnd >= :date " +
            "AND (t.maxParticipants IS NULL OR t.currentParticipants < t.maxParticipants) " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.registrationEnd")
    List<Tournament> findOpenForRegistrationByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    // Upcoming Tournaments
    @Query("SELECT t FROM Tournament t WHERE t.startDate > :date " +
            "AND t.status IN ('OPEN', 'REGISTRATION_CLOSED') " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    List<Tournament> findUpcomingTournaments(@Param("date") LocalDate date);

    @Query("SELECT t FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.startDate > :date " +
            "AND t.status IN ('OPEN', 'REGISTRATION_CLOSED') " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    List<Tournament> findUpcomingTournamentsByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT t FROM Tournament t WHERE t.startDate > :date " +
            "AND t.status IN ('OPEN', 'REGISTRATION_CLOSED') " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    Page<Tournament> findUpcomingTournaments(@Param("date") LocalDate date, Pageable pageable);

    // Ongoing Tournaments
    @Query("SELECT t FROM Tournament t WHERE t.status = 'IN_PROGRESS' " +
            "AND t.startDate <= :date " +
            "AND t.endDate >= :date " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    List<Tournament> findOngoingTournaments(@Param("date") LocalDate date);

    @Query("SELECT t FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.status = 'IN_PROGRESS' " +
            "AND t.startDate <= :date " +
            "AND t.endDate >= :date " +
            "AND t.deletedAt IS NULL")
    List<Tournament> findOngoingTournamentsByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    // Completed Tournaments
    @Query("SELECT t FROM Tournament t WHERE t.status = 'COMPLETED' " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.endDate DESC")
    Page<Tournament> findCompletedTournaments(Pageable pageable);

    @Query("SELECT t FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.status = 'COMPLETED' " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.endDate DESC")
    Page<Tournament> findCompletedTournamentsByBranch(@Param("branchId") Long branchId, Pageable pageable);

    // Date Range Queries
    List<Tournament> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.startDate BETWEEN :startDate AND :endDate " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    List<Tournament> findByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Tournament t WHERE " +
            "(t.startDate BETWEEN :startDate AND :endDate " +
            "OR t.endDate BETWEEN :startDate AND :endDate " +
            "OR (t.startDate <= :startDate AND t.endDate >= :endDate)) " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    List<Tournament> findTournamentsInPeriod(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Featured Tournaments
    @Query("SELECT t FROM Tournament t WHERE t.isFeatured = true " +
            "AND t.status IN ('OPEN', 'IN_PROGRESS') " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    List<Tournament> findFeaturedTournaments();

    @Query("SELECT t FROM Tournament t WHERE t.isFeatured = true " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate DESC")
    List<Tournament> findAllFeaturedTournaments(Pageable pageable);

    // Public Tournaments
    @Query("SELECT t FROM Tournament t WHERE t.isPublic = true " +
            "AND t.status IN ('OPEN', 'IN_PROGRESS') " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.startDate")
    Page<Tournament> findPublicTournaments(Pageable pageable);

    // Search Queries
    @Query("SELECT t FROM Tournament t WHERE " +
            "(LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.tournamentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND t.deletedAt IS NULL")
    Page<Tournament> searchTournaments(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND (LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.tournamentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND t.deletedAt IS NULL")
    Page<Tournament> searchTournamentsByBranch(@Param("branchId") Long branchId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Organizer Queries
    List<Tournament> findByOrganizerId(Long organizerId);

    Page<Tournament> findByOrganizerId(Long organizerId, Pageable pageable);

    @Query("SELECT t FROM Tournament t WHERE t.organizer.id = :organizerId " +
            "AND t.status IN ('OPEN', 'IN_PROGRESS') " +
            "AND t.deletedAt IS NULL")
    List<Tournament> findActiveTournamentsByOrganizer(@Param("organizerId") Long organizerId);

    // Entry Fee Range
    @Query("SELECT t FROM Tournament t WHERE t.entryFee BETWEEN :minFee AND :maxFee " +
            "AND t.status = 'OPEN' " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.entryFee")
    List<Tournament> findByEntryFeeRange(@Param("minFee") BigDecimal minFee,
            @Param("maxFee") BigDecimal maxFee);

    // Prize Pool Range
    @Query("SELECT t FROM Tournament t WHERE t.prizePool >= :minPrize " +
            "AND t.status IN ('OPEN', 'IN_PROGRESS') " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.prizePool DESC")
    List<Tournament> findByMinimumPrizePool(@Param("minPrize") BigDecimal minPrize);

    // Capacity Queries
    @Query("SELECT t FROM Tournament t WHERE t.maxParticipants IS NOT NULL " +
            "AND t.currentParticipants >= t.maxParticipants " +
            "AND t.status = 'OPEN' " +
            "AND t.deletedAt IS NULL")
    List<Tournament> findFullTournaments();

    @Query("SELECT t FROM Tournament t WHERE t.currentParticipants < t.minParticipants " +
            "AND t.startDate <= :date " +
            "AND t.status = 'OPEN' " +
            "AND t.deletedAt IS NULL")
    List<Tournament> findTournamentsNotMeetingMinimum(@Param("date") LocalDate date);

    // Statistics Queries
    @Query("SELECT COUNT(t) FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.status = :status " +
            "AND t.deletedAt IS NULL")
    long countByBranchAndStatus(@Param("branchId") Long branchId,
            @Param("status") TournamentStatus status);

    @Query("SELECT COUNT(t) FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.startDate BETWEEN :startDate AND :endDate " +
            "AND t.deletedAt IS NULL")
    long countByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(t.currentParticipants) FROM Tournament t " +
            "WHERE t.branch.id = :branchId " +
            "AND t.startDate BETWEEN :startDate AND :endDate " +
            "AND t.deletedAt IS NULL")
    Long sumTotalParticipantsByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(t.totalCollected) FROM Tournament t " +
            "WHERE t.branch.id = :branchId " +
            "AND t.status = 'COMPLETED' " +
            "AND t.deletedAt IS NULL")
    BigDecimal sumTotalRevenueByBranch(@Param("branchId") Long branchId);

    @Query("SELECT SUM(t.totalCollected) FROM Tournament t " +
            "WHERE t.branch.id = :branchId " +
            "AND t.startDate BETWEEN :startDate AND :endDate " +
            "AND t.status = 'COMPLETED' " +
            "AND t.deletedAt IS NULL")
    BigDecimal sumRevenueByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Type Statistics
    @Query("SELECT t.tournamentType, COUNT(t) FROM Tournament t " +
            "WHERE t.branch.id = :branchId " +
            "AND t.deletedAt IS NULL " +
            "GROUP BY t.tournamentType")
    List<Object[]> countTournamentsByType(@Param("branchId") Long branchId);

    @Query("SELECT t.tournamentFormat, COUNT(t) FROM Tournament t " +
            "WHERE t.branch.id = :branchId " +
            "AND t.deletedAt IS NULL " +
            "GROUP BY t.tournamentFormat")
    List<Object[]> countTournamentsByFormat(@Param("branchId") Long branchId);

    // Popular Tournaments
    @Query("SELECT t FROM Tournament t WHERE t.deletedAt IS NULL " +
            "ORDER BY t.currentParticipants DESC")
    List<Tournament> findMostPopularTournaments(Pageable pageable);

    @Query("SELECT t FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.viewCount DESC")
    List<Tournament> findMostViewedTournamentsByBranch(@Param("branchId") Long branchId, Pageable pageable);

    // Recent Tournaments
    @Query("SELECT t FROM Tournament t WHERE t.deletedAt IS NULL " +
            "ORDER BY t.createdAt DESC")
    List<Tournament> findRecentTournaments(Pageable pageable);

    @Query("SELECT t FROM Tournament t WHERE t.branch.id = :branchId " +
            "AND t.deletedAt IS NULL " +
            "ORDER BY t.createdAt DESC")
    List<Tournament> findRecentTournamentsByBranch(@Param("branchId") Long branchId, Pageable pageable);

    // Update Operations
    @Modifying
    @Query("UPDATE Tournament t SET t.currentParticipants = t.currentParticipants + 1 WHERE t.id = :tournamentId")
    void incrementParticipantCount(@Param("tournamentId") Long tournamentId);

    @Modifying
    @Query("UPDATE Tournament t SET t.currentParticipants = t.currentParticipants - 1 " +
            "WHERE t.id = :tournamentId AND t.currentParticipants > 0")
    void decrementParticipantCount(@Param("tournamentId") Long tournamentId);

    @Modifying
    @Query("UPDATE Tournament t SET t.viewCount = t.viewCount + 1 WHERE t.id = :tournamentId")
    void incrementViewCount(@Param("tournamentId") Long tournamentId);

    @Modifying
    @Query("UPDATE Tournament t SET t.totalMatches = t.totalMatches + 1 WHERE t.id = :tournamentId")
    void incrementTotalMatches(@Param("tournamentId") Long tournamentId);

    @Modifying
    @Query("UPDATE Tournament t SET t.completedMatches = t.completedMatches + 1 WHERE t.id = :tournamentId")
    void incrementCompletedMatches(@Param("tournamentId") Long tournamentId);

    // Monthly/Yearly Analysis
    @Query("SELECT YEAR(t.startDate), MONTH(t.startDate), COUNT(t) " +
            "FROM Tournament t " +
            "WHERE t.branch.id = :branchId " +
            "AND t.deletedAt IS NULL " +
            "GROUP BY YEAR(t.startDate), MONTH(t.startDate) " +
            "ORDER BY YEAR(t.startDate) DESC, MONTH(t.startDate) DESC")
    List<Object[]> getTournamentTrendByMonth(@Param("branchId") Long branchId);
}
