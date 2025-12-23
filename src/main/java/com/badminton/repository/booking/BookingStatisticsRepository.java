package com.badminton.repository.booking;

import com.badminton.entity.booking.BookingStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingStatisticsRepository extends JpaRepository<BookingStatistics, Long> {

    Optional<BookingStatistics> findByBranchIdAndDate(Long branchId, LocalDate date);

    List<BookingStatistics> findByBranchId(Long branchId);

    List<BookingStatistics> findByBranchIdAndDateBetween(Long branchId, LocalDate startDate, LocalDate endDate);

    List<BookingStatistics> findByDate(LocalDate date);

    @Query("SELECT bs FROM BookingStatistics bs WHERE bs.branch.id = :branchId " +
            "ORDER BY bs.date DESC")
    List<BookingStatistics> findRecentStatisticsByBranch(@Param("branchId") Long branchId,
            org.springframework.data.domain.Pageable pageable);
}
