package com.badminton.repository.booking;

import com.badminton.entity.booking.Booking;
import com.badminton.enums.BookingStatus;
import com.badminton.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    // Basic Queries
    Optional<Booking> findByBookingNumber(String bookingNumber);

    List<Booking> findByUserId(Long userId);

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    List<Booking> findByBranchId(Long branchId);

    Page<Booking> findByBranchId(Long branchId, Pageable pageable);

    List<Booking> findByCourtId(Long courtId);

    // Status Queries
    List<Booking> findByStatus(BookingStatus status);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findByBranchIdAndStatus(Long branchId, BookingStatus status);

    // Payment Status
    List<Booking> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Booking> findByStatusAndPaymentStatus(BookingStatus status, PaymentStatus paymentStatus);

    // Date Queries
    List<Booking> findByBookingDate(LocalDate bookingDate);

    List<Booking> findByBookingDateBetween(LocalDate startDate, LocalDate endDate);

    List<Booking> findByBranchIdAndBookingDate(Long branchId, LocalDate bookingDate);

    @Query("SELECT b FROM Booking b WHERE b.branch.id = :branchId " +
            "AND b.bookingDate = :date " +
            "AND b.status IN ('CONFIRMED', 'CHECKED_IN', 'IN_PROGRESS')")
    List<Booking> findActiveBookingsByBranchAndDate(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    // Time Slot Availability
    @Query("SELECT b FROM Booking b WHERE b.court.id = :courtId " +
            "AND b.bookingDate = :date " +
            "AND b.status NOT IN ('CANCELLED', 'NO_SHOW') " +
            "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookings(@Param("courtId") Long courtId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.court.id = :courtId " +
            "AND b.bookingDate = :date " +
            "AND b.status NOT IN ('CANCELLED', 'NO_SHOW') " +
            "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    boolean existsConflictingBooking(@Param("courtId") Long courtId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    // Statistics
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.branch.id = :branchId " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate " +
            "AND b.status = :status")
    long countByBranchAndDateRangeAndStatus(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") BookingStatus status);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.branch.id = :branchId " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate " +
            "AND b.status = 'COMPLETED'")
    BigDecimal sumRevenueByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(DISTINCT b.user.id) FROM Booking b WHERE b.branch.id = :branchId " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate")
    long countUniqueCustomersByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Upcoming Bookings
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
            "AND b.bookingDate >= :date " +
            "AND b.status IN ('PENDING', 'CONFIRMED') " +
            "ORDER BY b.bookingDate, b.startTime")
    List<Booking> findUpcomingBookingsByUser(@Param("userId") Long userId,
            @Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.branch.id = :branchId " +
            "AND b.bookingDate = :date " +
            "AND b.status IN ('CONFIRMED', 'CHECKED_IN') " +
            "ORDER BY b.startTime")
    List<Booking> findTodayBookingsByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    // Overdue Bookings
    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' " +
            "AND b.bookingDate < :date")
    List<Booking> findOverdueBookings(@Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' " +
            "AND b.paymentDeadline < :dateTime")
    List<Booking> findExpiredPendingBookings(@Param("dateTime") LocalDateTime dateTime);

    // Recurring Bookings
    List<Booking> findByRecurringBookingId(Long recurringBookingId);

    @Query("SELECT b FROM Booking b WHERE b.isRecurring = true " +
            "AND b.recurringBookingId IS NULL")
    List<Booking> findRecurringParentBookings();

    // Search
    @Query("SELECT b FROM Booking b WHERE " +
            "b.bookingNumber LIKE CONCAT('%', :keyword, '%') OR " +
            "b.customerName LIKE CONCAT('%', :keyword, '%') OR " +
            "b.customerPhone LIKE CONCAT('%', :keyword, '%')")
    Page<Booking> searchBookings(@Param("keyword") String keyword, Pageable pageable);

    // Peak Hours Analysis
    @Query("SELECT b.startTime, COUNT(b) FROM Booking b " +
            "WHERE b.branch.id = :branchId " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate " +
            "AND b.status = 'COMPLETED' " +
            "GROUP BY b.startTime " +
            "ORDER BY COUNT(b) DESC")
    List<Object[]> findPeakHoursByBranch(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
