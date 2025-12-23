package com.badminton.repository.audit;

import com.badminton.entity.audit.AuditLog;
import com.badminton.enums.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Basic Queries
    List<AuditLog> findByUserId(Long userId);

    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    List<AuditLog> findByUsername(String username);

    // Action Type Queries
    List<AuditLog> findByActionType(ActionType actionType);

    Page<AuditLog> findByActionType(ActionType actionType, Pageable pageable);

    List<AuditLog> findByUserIdAndActionType(Long userId, ActionType actionType);

    // Entity Queries
    List<AuditLog> findByEntityType(String entityType);

    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType " +
            "AND al.entityId = :entityId " +
            "ORDER BY al.createdAt DESC")
    List<AuditLog> findEntityHistory(@Param("entityType") String entityType,
            @Param("entityId") Long entityId);

    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType " +
            "AND al.entityId = :entityId " +
            "ORDER BY al.createdAt DESC")
    Page<AuditLog> findEntityHistory(@Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            Pageable pageable);

    // Success/Failure Queries
    List<AuditLog> findBySuccess(Boolean success);

    Page<AuditLog> findBySuccess(Boolean success, Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.success = false " +
            "ORDER BY al.createdAt DESC")
    List<AuditLog> findFailedActions(Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId " +
            "AND al.success = false " +
            "ORDER BY al.createdAt DESC")
    List<AuditLog> findFailedActionsByUser(@Param("userId") Long userId);

    // Date Range Queries
    List<AuditLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId " +
            "AND al.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY al.createdAt DESC")
    List<AuditLog> findByUserAndDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType " +
            "AND al.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY al.createdAt DESC")
    List<AuditLog> findByEntityTypeAndDateRange(@Param("entityType") String entityType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // IP Address Queries
    List<AuditLog> findByIpAddress(String ipAddress);

    @Query("SELECT al FROM AuditLog al WHERE al.ipAddress = :ipAddress " +
            "ORDER BY al.createdAt DESC")
    Page<AuditLog> findByIpAddress(@Param("ipAddress") String ipAddress, Pageable pageable);

    @Query("SELECT al.ipAddress, COUNT(al) FROM AuditLog al " +
            "WHERE al.createdAt >= :date " +
            "GROUP BY al.ipAddress " +
            "ORDER BY COUNT(al) DESC")
    List<Object[]> findMostActiveIpAddresses(@Param("date") LocalDateTime date,
            Pageable pageable);

    // Session Queries
    List<AuditLog> findBySessionId(String sessionId);

    @Query("SELECT al FROM AuditLog al WHERE al.sessionId = :sessionId " +
            "ORDER BY al.createdAt")
    List<AuditLog> findSessionActivity(@Param("sessionId") String sessionId);

    // Recent Activity
    @Query("SELECT al FROM AuditLog al ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentActivity(Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId " +
            "ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentUserActivity(@Param("userId") Long userId, Pageable pageable);

    // Search Queries
    @Query("SELECT al FROM AuditLog al WHERE " +
            "LOWER(al.actionDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(al.entityName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<AuditLog> searchLogs(@Param("keyword") String keyword, Pageable pageable);

    // Statistics
    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.actionType = :actionType " +
            "AND al.createdAt >= :date")
    long countByActionTypeAfter(@Param("actionType") ActionType actionType,
            @Param("date") LocalDateTime date);

    @Query("SELECT al.actionType, COUNT(al) FROM AuditLog al " +
            "WHERE al.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY al.actionType " +
            "ORDER BY COUNT(al) DESC")
    List<Object[]> getActionTypeStatistics(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT al.entityType, COUNT(al) FROM AuditLog al " +
            "WHERE al.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY al.entityType " +
            "ORDER BY COUNT(al) DESC")
    List<Object[]> getEntityTypeStatistics(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT al.user, COUNT(al) FROM AuditLog al " +
            "WHERE al.createdAt >= :date " +
            "AND al.user IS NOT NULL " +
            "GROUP BY al.user " +
            "ORDER BY COUNT(al) DESC")
    List<Object[]> findMostActiveUsers(@Param("date") LocalDateTime date, Pageable pageable);

    // Performance Monitoring
    @Query("SELECT AVG(al.executionTimeMs) FROM AuditLog al " +
            "WHERE al.actionType = :actionType " +
            "AND al.createdAt >= :date")
    Double getAverageExecutionTime(@Param("actionType") ActionType actionType,
            @Param("date") LocalDateTime date);

    @Query("SELECT al FROM AuditLog al WHERE al.executionTimeMs > :threshold " +
            "ORDER BY al.executionTimeMs DESC")
    List<AuditLog> findSlowOperations(@Param("threshold") Long threshold, Pageable pageable);

    // Hourly Activity
    @Query("SELECT HOUR(al.createdAt), COUNT(al) FROM AuditLog al " +
            "WHERE al.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY HOUR(al.createdAt) " +
            "ORDER BY HOUR(al.createdAt)")
    List<Object[]> getHourlyActivityDistribution(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Daily Activity
    @Query("SELECT DATE(al.createdAt), COUNT(al) FROM AuditLog al " +
            "WHERE al.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(al.createdAt) " +
            "ORDER BY DATE(al.createdAt)")
    List<Object[]> getDailyActivityTrend(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
