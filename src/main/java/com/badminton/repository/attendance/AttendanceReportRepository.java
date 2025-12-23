package com.badminton.repository.attendance;

import com.badminton.entity.attendance.AttendanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceReportRepository extends JpaRepository<AttendanceReport, Long> {

    // Basic Queries
    List<AttendanceReport> findByBranchId(Long branchId);

    List<AttendanceReport> findByUserId(Long userId);

    Optional<AttendanceReport> findByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);

    boolean existsByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);

    // Year/Month Queries
    List<AttendanceReport> findByYear(Integer year);

    List<AttendanceReport> findByYearAndMonth(Integer year, Integer month);

    List<AttendanceReport> findByBranchIdAndYear(Long branchId, Integer year);

    List<AttendanceReport> findByBranchIdAndYearAndMonth(Long branchId, Integer year, Integer month);

    @Query("SELECT ar FROM AttendanceReport ar WHERE ar.user.id = :userId " +
            "ORDER BY ar.year DESC, ar.month DESC")
    List<AttendanceReport> findUserReportHistory(@Param("userId") Long userId);

    // Statistics
    @Query("SELECT AVG(ar.attendanceRate) FROM AttendanceReport ar " +
            "WHERE ar.branch.id = :branchId " +
            "AND ar.year = :year " +
            "AND ar.month = :month")
    BigDecimal getAverageAttendanceRate(@Param("branchId") Long branchId,
            @Param("year") Integer year,
            @Param("month") Integer month);

    @Query("SELECT AVG(ar.punctualityRate) FROM AttendanceReport ar " +
            "WHERE ar.branch.id = :branchId " +
            "AND ar.year = :year " +
            "AND ar.month = :month")
    BigDecimal getAveragePunctualityRate(@Param("branchId") Long branchId,
            @Param("year") Integer year,
            @Param("month") Integer month);

    @Query("SELECT SUM(ar.totalHoursWorked) FROM AttendanceReport ar " +
            "WHERE ar.branch.id = :branchId " +
            "AND ar.year = :year " +
            "AND ar.month = :month")
    BigDecimal sumTotalHoursWorked(@Param("branchId") Long branchId,
            @Param("year") Integer year,
            @Param("month") Integer month);

    @Query("SELECT SUM(ar.overtimeHours) FROM AttendanceReport ar " +
            "WHERE ar.branch.id = :branchId " +
            "AND ar.year = :year " +
            "AND ar.month = :month")
    BigDecimal sumOvertimeHours(@Param("branchId") Long branchId,
            @Param("year") Integer year,
            @Param("month") Integer month);

    // Top Performers
    @Query("SELECT ar FROM AttendanceReport ar WHERE ar.branch.id = :branchId " +
            "AND ar.year = :year " +
            "AND ar.month = :month " +
            "ORDER BY ar.attendanceRate DESC")
    List<AttendanceReport> findTopAttendanceByMonth(@Param("branchId") Long branchId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT ar FROM AttendanceReport ar WHERE ar.branch.id = :branchId " +
            "AND ar.year = :year " +
            "AND ar.month = :month " +
            "ORDER BY ar.punctualityRate DESC")
    List<AttendanceReport> findTopPunctualityByMonth(@Param("branchId") Long branchId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            org.springframework.data.domain.Pageable pageable);

    // Trend Analysis
    @Query("SELECT ar.year, ar.month, AVG(ar.attendanceRate) " +
            "FROM AttendanceReport ar " +
            "WHERE ar.branch.id = :branchId " +
            "GROUP BY ar.year, ar.month " +
            "ORDER BY ar.year, ar.month")
    List<Object[]> getAttendanceTrend(@Param("branchId") Long branchId);
}
