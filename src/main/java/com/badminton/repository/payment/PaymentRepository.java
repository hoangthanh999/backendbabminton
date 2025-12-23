package com.badminton.repository.payment;

import com.badminton.entity.payment.Payment;
import com.badminton.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByReferenceNumber(String referenceNumber);

    List<Payment> findByUserId(Long userId);

    Page<Payment> findByUserId(Long userId, Pageable pageable);

    List<Payment> findByBranchId(Long branchId);

    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByStatus(PaymentStatus status);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    List<Payment> findByMethodId(Long methodId);

    @Query("SELECT p FROM Payment p WHERE p.branch.id = :branchId " +
            "AND p.createdAt BETWEEN :startDate AND :endDate " +
            "AND p.status = :status")
    List<Payment> findByBranchAndDateRangeAndStatus(@Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.branch.id = :branchId " +
            "AND p.status = 'COMPLETED' " +
            "AND p.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal sumCompletedPaymentsByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' " +
            "AND p.expiredAt < :dateTime")
    List<Payment> findExpiredPendingPayments(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT p.method.name, COUNT(p), SUM(p.amount) FROM Payment p " +
            "WHERE p.branch.id = :branchId " +
            "AND p.status = 'COMPLETED' " +
            "AND p.paidAt BETWEEN :startDate AND :endDate " +
            "GROUP BY p.method.name")
    List<Object[]> getPaymentMethodStatistics(@Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
