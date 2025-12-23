package com.badminton.repository.order;

import com.badminton.entity.order.OrderItem;
import com.badminton.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    List<OrderItem> findByServiceId(Long serviceId);

    List<OrderItem> findByItemIdAndItemType(Long itemId, ItemType itemType);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.branch.id = :branchId " +
            "AND oi.order.createdAt BETWEEN :startDate AND :endDate " +
            "AND oi.itemType = 'PRODUCT' " +
            "GROUP BY oi.product.id " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<OrderItem> findTopSellingProducts(@Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            org.springframework.data.domain.Pageable pageable);
}
