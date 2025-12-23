package com.badminton.repository.attendance;

import com.badminton.entity.attendance.Attendance;
import com.badminton.enums.AttendanceStatus;
import com.badminton.enums.AttendanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Basic Queries
    List<Attendance> findByBranchId(Long branchId);

    List<Attendance> findByUserId(Long userId);

    Page<Attendance> findByUserId(Long userId, Pageable pageable);

    Optional<Attendance> findByUserIdAndDate(Long userId, LocalDate date);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);

    // Status Queries
    List<Attendance> findByStatus(AttendanceStatus status);

    List<Attendance> findByBranchIdAndStatus(Long branchId, AttendanceStatus status);

    List<Attendance> findByUserIdAndStatus(Long userId, AttendanceStatus status);

    // Type Queries
    List<Attendance> findByAttendanceType(AttendanceType type);

    List<Attendance> findByBranchIdAndAttendanceType(Long branchId, AttendanceType type);

    // Date Queries
    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByBranchIdAndDate(Long branchId, LocalDate date);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Attendance> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.branch.id = :branchId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "ORDER BY a.date, a.user.name")
    List<Attendance> findByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Today's Attendance
    @Query("SELECT a FROM Attendance a WHERE a.branch.id = :branchId " +
            "AND a.date = :date " +
            "ORDER BY a.checkInTime")
    List<Attendance> findTodayAttendance(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.date = :date " +
            "AND a.status = 'PRESENT' " +
            "ORDER BY a.checkInTime")
    List<Attendance> findTodayPresentAttendance(@Param("date") LocalDate date);

    // Check-in/Check-out Status
    @Query("SELECT a FROM Attendance a WHERE a.branch.id = :branchId " +
            "AND a.date = :date " +
            "AND a.checkInTime IS NOT NULL " +
            "AND a.checkOutTime IS NULL")
    List<Attendance> findNotCheckedOut(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId " +
            "AND a.checkInTime IS NOT NULL " +
            "AND a.checkOutTime IS NULL " +
            "ORDER BY a.date DESC")
    Optional<Attendance> findActiveAttendance(@Param("userId") Long userId);

    // Late Attendance
    @Query("SELECT a FROM Attendance a WHERE a.branch.id = :branchId " +
            "AND a.date = :date " +
            "AND a.isLate = true")
    List<Attendance> findLateAttendance(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId " +
            "AND a.isLate = true " +
            "AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findUserLateAttendance(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Absent
    @Query("SELECT a FROM Attendance a WHERE a.branch.id = :branchId " +
            "AND a.date = :date " +
            "AND a.status = 'ABSENT'")
    List<Attendance> findAbsentAttendance(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    // Leave Requests
    @Query("SELECT a FROM Attendance a WHERE a.status = 'ON_LEAVE' " +
            "AND a.leaveApproved = false " +
            "ORDER BY a.date")
    List<Attendance> findPendingLeaveRequests();

    @Query("SELECT a FROM Attendance a WHERE a.branch.id = :branchId " +
            "AND a.status = 'ON_LEAVE' " +
            "AND a.leaveApproved = false " +
            "ORDER BY a.date")
    List<Attendance> findPendingLeaveRequestsByBranch(@Param("branchId") Long branchId);

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId " +
            "AND a.status = 'ON_LEAVE' " +
            "ORDER BY a.date DESC")
    List<Attendance> findUserLeaveHistory(@Param("userId") Long userId);

    List<Attendance> findByLeaveApprovedById(Long approverId);

    // Verification Status
    @Query("SELECT a FROM Attendance a WHERE a.branch.id = :branchId " +
            "AND a.isVerified = false " +
            "AND a.date <= :date " +
            "ORDER BY a.date")
    List<Attendance> findUnverifiedAttendance(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    List<Attendance> findByVerifiedById(Long verifierId);

    // Statistics Queries
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId " +
            "AND a.status = :status " +
            "AND a.date BETWEEN :startDate AND :endDate")
    long countByUserAndStatusAndDateRange(@Param("userId") Long userId,
            @Param("status") AttendanceStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.branch.id = :branchId " +
            "AND a.date = :date " +
            "AND a.status = 'PRESENT'")
    long countPresentByBranchAndDate(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.branch.id = :branchId " +
            "AND a.date = :date " +
            "AND a.status = 'ABSENT'")
    long countAbsentByBranchAndDate(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId " +
            "AND a.isLate = true " +
            "AND a.date BETWEEN :startDate AND :endDate")
    long countLateByUserAndDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(a.totalHours) FROM Attendance a WHERE a.user.id = :userId " +
            "AND a.date BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalHoursByUserAndDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(a.overtimeHours) FROM Attendance a WHERE a.user.id = :userId " +
            "AND a.date BETWEEN :startDate AND :endDate")
    BigDecimal sumOvertimeHoursByUserAndDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Monthly Report Data
    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId " +
            "AND YEAR(a.date) = :year " +
            "AND MONTH(a.date) = :month " +
            "ORDER BY a.date")
    List<Attendance> findMonthlyAttendance(@Param("userId") Long userId,
            @Param("year") Integer year,
            @Param("month") Integer month);

    // Attendance Rate
    @Query("SELECT a.status, COUNT(a) FROM Attendance a " +
            "WHERE a.user.id = :userId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "GROUP BY a.status")
    List<Object[]> getAttendanceStatusDistribution(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Team Attendance
    @Query("SELECT a.user, COUNT(a) FROM Attendance a " +
            "WHERE a.branch.id = :branchId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.status = 'PRESENT' " +
            "GROUP BY a.user " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> getTeamAttendanceStats(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Peak Hours
    @Query("SELECT HOUR(a.checkInTime), COUNT(a) FROM Attendance a " +
            "WHERE a.branch.id = :branchId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.checkInTime IS NOT NULL " +
            "GROUP BY HOUR(a.checkInTime) " +
            "ORDER BY HOUR(a.checkInTime)")
    List<Object[]> getCheckInHourDistribution(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
