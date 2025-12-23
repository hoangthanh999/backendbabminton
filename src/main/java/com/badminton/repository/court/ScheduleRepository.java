package com.badminton.repository.court;

import com.badminton.entity.court.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByCourtId(Long courtId);

    List<Schedule> findByCourtIdAndDayOfWeek(Long courtId, DayOfWeek dayOfWeek);

    Optional<Schedule> findByCourtIdAndDayOfWeekAndStartTime(Long courtId, DayOfWeek dayOfWeek, LocalTime startTime);

    @Query("SELECT s FROM Schedule s WHERE s.court.id = :courtId " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND s.startTime <= :time " +
            "AND s.endTime > :time")
    Optional<Schedule> findScheduleAtTime(@Param("courtId") Long courtId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time);

    @Query("SELECT s FROM Schedule s WHERE s.court.branch.id = :branchId AND s.dayOfWeek = :dayOfWeek")
    List<Schedule> findByBranchAndDayOfWeek(@Param("branchId") Long branchId, @Param("dayOfWeek") DayOfWeek dayOfWeek);
}
