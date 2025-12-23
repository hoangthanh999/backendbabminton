package com.badminton.repository.order;

import com.badminton.entity.order.Order;
import com.badminton.enums.OrderStatus;
import com.badminton.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserId(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByBranchId(Long branchId);

    Page<Order> findByBranchId(Long branchId, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.branch.id = :branchId " +
            "AND o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status = :status")
    List<Order> findByBranchAndDateRangeAndStatus(@Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.branch.id = :branchId " +
            "AND o.status = 'COMPLETED' " +
            "AND o.completedAt BETWEEN :startDate AND :endDate")
    BigDecimal sumRevenueByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.orderNumber LIKE CONCAT('%', :keyword, '%') OR " +
            "o.customerName LIKE CONCAT('%', :keyword, '%') OR " +
            "o.customerPhone LIKE CONCAT('%', :keyword, '%')")
    Page<Order> searchOrders(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED') " +
            "AND o.createdAt < :dateTime")
    List<Order> findOldPendingOrders(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.branch.id = :branchId AND o.status = :status")
    long countByBranchAndStatus(@Param("branchId") Long branchId, @Param("status") OrderStatus status);
}
