package com.badminton.util;

import com.badminton.entity.booking.Booking;
import com.badminton.entity.court.Court;
import com.badminton.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class BookingUtils {

    /**
     * Check if time slots overlap
     */
    public static boolean isTimeSlotOverlap(
            LocalTime start1, LocalTime end1,
            LocalTime start2, LocalTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    /**
     * Check if booking conflicts with existing bookings
     */
    public static boolean hasConflict(
            Court court,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            List<Booking> existingBookings) {
        return existingBookings.stream()
                .filter(b -> b.getCourt().getId().equals(court.getId()))
                .filter(b -> b.getDate().equals(date))
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED ||
                        b.getStatus() == BookingStatus.PENDING)
                .anyMatch(b -> isTimeSlotOverlap(
                        b.getTimeStart(), b.getTimeEnd(),
                        startTime, endTime));
    }

    /**
     * Calculate cancellation fee
     */
    public static BigDecimal calculateCancellationFee(
            Booking booking,
            LocalDateTime cancellationTime) {
        LocalDateTime bookingDateTime = LocalDateTime.of(
                booking.getDate(),
                booking.getTimeStart());

        long hoursUntilBooking = java.time.Duration
                .between(cancellationTime, bookingDateTime)
                .toHours();

        // Free cancellation if more than 24 hours
        if (hoursUntilBooking >= 24) {
            return BigDecimal.ZERO;
        }

        // 50% fee if 12-24 hours
        if (hoursUntilBooking >= 12) {
            return booking.getDepositAmount()
                    .multiply(new BigDecimal("0.50"));
        }

        // 100% deposit fee if less than 12 hours
        return booking.getDepositAmount();
    }

    /**
     * Calculate loyalty points
     */
    public static int calculateLoyaltyPoints(BigDecimal amount) {
        // 1 point per 10,000 VND
        return amount.divide(
                new BigDecimal("10000"),
                0,
                java.math.RoundingMode.DOWN).intValue();
    }

    /**
     * Validate booking time
     */
    public static void validateBookingTime(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            int minDuration,
            int maxDuration) {
        // Check if date is in the past
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book in the past");
        }

        // Check if start time is before end time
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check duration
        long duration = java.time.Duration.between(startTime, endTime).toMinutes();

        if (duration < minDuration) {
            throw new IllegalArgumentException(
                    String.format("Minimum booking duration is %d minutes", minDuration));
        }

        if (duration > maxDuration) {
            throw new IllegalArgumentException(
                    String.format("Maximum booking duration is %d minutes", maxDuration));
        }
    }

    /**
     * Check if booking can be modified
     */
    public static boolean canBeModified(Booking booking) {
        if (booking.getStatus() != BookingStatus.PENDING &&
                booking.getStatus() != BookingStatus.CONFIRMED) {
            return false;
        }

        LocalDateTime bookingDateTime = LocalDateTime.of(
                booking.getDate(),
                booking.getTimeStart());

        // Can modify if more than 2 hours before start time
        return LocalDateTime.now().plusHours(2).isBefore(bookingDateTime);
    }

    /**
     * Generate booking reference
     */
    public static String generateReference(Long bookingId, LocalDate date) {
        return String.format("BK%s%06d",
                date.toString().replace("-", ""),
                bookingId != null ? bookingId : 0);
    }

    /**
     * Calculate total booking cost including services and equipment
     */
    public static BigDecimal calculateTotalCost(Booking booking) {
        BigDecimal total = booking.getTotalAmount();

        // Add equipment costs
        total = total.add(
                booking.getEquipments().stream()
                        .map(e -> e.getTotalPrice())
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Add service costs
        total = total.add(
                booking.getServices().stream()
                        .map(s -> s.getTotalPrice())
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        return total;
    }
}
