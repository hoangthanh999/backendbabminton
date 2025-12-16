package com.badminton.util;

import com.badminton.entity.court.Court;
import com.badminton.entity.court.CourtPricing;
import com.badminton.entity.court.Schedule;
import com.badminton.enums.DayOfWeek;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class CourtUtils {

    /**
     * Find pricing for specific day and time
     */
    public static Optional<CourtPricing> findPricing(Court court, DayOfWeek dayOfWeek, LocalTime time) {
        return court.getPricings().stream()
                .filter(pricing -> pricing.getDayOfWeek() == dayOfWeek)
                .filter(pricing -> pricing.isTimeInRange(time))
                .filter(CourtPricing::getIsActive)
                .findFirst();
    }

    /**
     * Calculate booking price
     */
    public static BigDecimal calculateBookingPrice(
            Court court,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {
        DayOfWeek dayOfWeek = DayOfWeek.fromJavaTime(date.getDayOfWeek());

        Optional<CourtPricing> pricing = findPricing(court, dayOfWeek, startTime);

        if (pricing.isEmpty()) {
            throw new IllegalArgumentException("No pricing found for the specified time");
        }

        long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return pricing.get().calculatePrice((int) durationMinutes);
    }

    /**
     * Find schedule for specific day
     */
    public static Optional<Schedule> findSchedule(Court court, DayOfWeek dayOfWeek) {
        return court.getSchedules().stream()
                .filter(schedule -> schedule.getDayOfWeek() == dayOfWeek)
                .filter(Schedule::getIsAvailable)
                .findFirst();
    }

    /**
     * Check if court is available at specific time
     */
    public static boolean isCourtAvailable(
            Court court,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {
        // Check court status
        if (!court.isAvailable()) {
            return false;
        }

        // Check maintenance
        boolean underMaintenance = court.getMaintenances().stream()
                .anyMatch(maintenance -> maintenance.isDateInMaintenancePeriod(date) &&
                        maintenance.isOngoing());

        if (underMaintenance) {
            return false;
        }

        // Check schedule
        DayOfWeek dayOfWeek = DayOfWeek.fromJavaTime(date.getDayOfWeek());
        Optional<Schedule> schedule = findSchedule(court, dayOfWeek);

        return schedule.isPresent() && schedule.get().isTimeSlotValid(startTime, endTime);
    }

    /**
     * Get available time slots for a date
     */
    public static List<Schedule.TimeSlot> getAvailableTimeSlots(Court court, LocalDate date) {
        DayOfWeek dayOfWeek = DayOfWeek.fromJavaTime(date.getDayOfWeek());

        return findSchedule(court, dayOfWeek)
                .map(Schedule::generateTimeSlots)
                .orElse(List.of());
    }
}
