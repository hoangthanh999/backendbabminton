package com.badminton.entity.court;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.booking.Booking;
import com.badminton.enums.DayOfWeek;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "schedules", indexes = {
        @Index(name = "idx_court_day", columnList = "court_id, day_of_week"),
        @Index(name = "idx_court_available", columnList = "court_id, is_available"),
        @Index(name = "idx_court_day_time", columnList = "court_id, day_of_week, time_start, time_end")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek; // 1=Monday, 7=Sunday

    @Column(name = "time_start", nullable = false)
    private LocalTime timeStart;

    @Column(name = "time_end", nullable = false)
    private LocalTime timeEnd;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "slot_duration")
    @Builder.Default
    private Integer slotDuration = 60; // minutes per slot

    @Column(name = "max_advance_booking_days")
    @Builder.Default
    private Integer maxAdvanceBookingDays = 30;

    @Column(name = "min_advance_booking_hours")
    @Builder.Default
    private Integer minAdvanceBookingHours = 2;

    @Column(name = "notes")
    private String notes;

    // Relationships
    @OneToMany(mappedBy = "schedule")
    @Builder.Default
    private Set<Booking> bookings = new HashSet<>();

    // Helper Methods

    /**
     * Check if time slot is within schedule
     */
    public boolean isTimeSlotValid(LocalTime start, LocalTime end) {
        return !start.isBefore(timeStart)
                && !end.isAfter(timeEnd)
                && start.isBefore(end);
    }

    /**
     * Get total available hours
     */
    public double getTotalHours() {
        long minutes = java.time.Duration.between(timeStart, timeEnd).toMinutes();
        return minutes / 60.0;
    }

    /**
     * Get number of available slots
     */
    public int getNumberOfSlots() {
        if (slotDuration == null || slotDuration == 0) {
            return 0;
        }
        long totalMinutes = java.time.Duration.between(timeStart, timeEnd).toMinutes();
        return (int) (totalMinutes / slotDuration);
    }

    /**
     * Generate time slots
     */
    public java.util.List<TimeSlot> generateTimeSlots() {
        java.util.List<TimeSlot> slots = new java.util.ArrayList<>();
        LocalTime current = timeStart;

        while (current.plusMinutes(slotDuration).isBefore(timeEnd) ||
                current.plusMinutes(slotDuration).equals(timeEnd)) {
            LocalTime slotEnd = current.plusMinutes(slotDuration);
            slots.add(new TimeSlot(current, slotEnd));
            current = slotEnd;
        }

        return slots;
    }

    /**
     * Check if schedule overlaps with another
     */
    public boolean overlapsWith(Schedule other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        return !(this.timeEnd.isBefore(other.timeStart) ||
                this.timeStart.isAfter(other.timeEnd));
    }

    /**
     * Get formatted time range
     */
    public String getTimeRange() {
        return String.format("%s - %s", timeStart, timeEnd);
    }

    /**
     * Get day name in Vietnamese
     */
    public String getDayNameVi() {
        return dayOfWeek.getVietnameseName();
    }

    // Inner class for time slot
    @Getter
    @AllArgsConstructor
    public static class TimeSlot {
        private LocalTime start;
        private LocalTime end;

        @Override
        public String toString() {
            return String.format("%s - %s", start, end);
        }
    }
}
