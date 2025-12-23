package com.badminton.dto.request.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Booking search/filter request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSearchRequest {

    private String keyword;
    private Long courtId;
    private Long userId;
    private Long branchId;

    private LocalDate dateFrom;
    private LocalDate dateTo;

    private String status;
    private String paymentStatus;
    private String bookingType;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
