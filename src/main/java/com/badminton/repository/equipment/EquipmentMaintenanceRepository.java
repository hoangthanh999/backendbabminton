package com.badminton.repository.equipment;

import com.badminton.entity.equipment.EquipmentMaintenance;
import com.badminton.enums.MaintenanceStatus;
import com.badminton.enums.MaintenanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EquipmentMaintenanceRepository extends JpaRepository<EquipmentMaintenance, Long> {

    // Basic Queries
    List<EquipmentMaintenance> findByEquipmentId(Long equipmentId);

    Page<EquipmentMaintenance> findByEquipmentId(Long equipmentId, Pageable pageable);

    // Status Queries
    List<EquipmentMaintenance> findByStatus(MaintenanceStatus status);

    Page<EquipmentMaintenance> findByStatus(MaintenanceStatus status, Pageable pageable);

    List<EquipmentMaintenance> findByEquipmentIdAndStatus(Long equipmentId, MaintenanceStatus status);

    // Type Queries
    List<EquipmentMaintenance> findByMaintenanceType(MaintenanceType type);

    List<EquipmentMaintenance> findByEquipmentIdAndMaintenanceType(Long equipmentId, MaintenanceType type);

    // Scheduled Maintenance
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.equipment.branch.id = :branchId " +
            "AND em.status = 'SCHEDULED' " +
            "AND em.scheduledDate BETWEEN :startDate AND :endDate " +
            "ORDER BY em.scheduledDate")
    List<EquipmentMaintenance> findScheduledMaintenanceByBranchAndDateRange(
            @Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.status = 'SCHEDULED' " +
            "AND em.scheduledDate = :date")
    List<EquipmentMaintenance> findScheduledMaintenanceForDate(@Param("date") LocalDate date);

    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.equipment.branch.id = :branchId " +
            "AND em.status = 'SCHEDULED' " +
            "AND em.scheduledDate = :date")
    List<EquipmentMaintenance> findScheduledMaintenanceByBranchAndDate(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    // Overdue Maintenance
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.status = 'SCHEDULED' " +
            "AND em.scheduledDate < :date")
    List<EquipmentMaintenance> findOverdueMaintenance(@Param("date") LocalDate date);

    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.equipment.branch.id = :branchId " +
            "AND em.status = 'SCHEDULED' " +
            "AND em.scheduledDate < :date")
    List<EquipmentMaintenance> findOverdueMaintenanceByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    // In Progress
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.equipment.branch.id = :branchId " +
            "AND em.status = 'IN_PROGRESS'")
    List<EquipmentMaintenance> findInProgressMaintenanceByBranch(@Param("branchId") Long branchId);

    // Completed Maintenance
    List<EquipmentMaintenance> findByStatusAndCompletedDateBetween(MaintenanceStatus status,
            LocalDate startDate,
            LocalDate endDate);

    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.equipment.branch.id = :branchId " +
            "AND em.status = 'COMPLETED' " +
            "AND em.completedDate BETWEEN :startDate AND :endDate")
    List<EquipmentMaintenance> findCompletedMaintenanceByBranchAndDateRange(
            @Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Cost Analysis
    @Query("SELECT SUM(em.cost) FROM EquipmentMaintenance em " +
            "WHERE em.equipment.branch.id = :branchId " +
            "AND em.status = 'COMPLETED' " +
            "AND em.completedDate BETWEEN :startDate AND :endDate")
    BigDecimal sumMaintenanceCostByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(em.cost) FROM EquipmentMaintenance em " +
            "WHERE em.equipment.id = :equipmentId " +
            "AND em.status = 'COMPLETED'")
    BigDecimal sumMaintenanceCostByEquipment(@Param("equipmentId") Long equipmentId);

    @Query("SELECT em.maintenanceType, COUNT(em), SUM(em.cost) FROM EquipmentMaintenance em " +
            "WHERE em.equipment.branch.id = :branchId " +
            "AND em.status = 'COMPLETED' " +
            "AND em.completedDate BETWEEN :startDate AND :endDate " +
            "GROUP BY em.maintenanceType")
    List<Object[]> getMaintenanceStatisticsByType(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Equipment with Most Maintenance
    @Query("SELECT em.equipment, COUNT(em), SUM(em.cost) FROM EquipmentMaintenance em " +
            "WHERE em.equipment.branch.id = :branchId " +
            "AND em.status = 'COMPLETED' " +
            "GROUP BY em.equipment " +
            "ORDER BY COUNT(em) DESC")
    List<Object[]> findEquipmentWithMostMaintenance(@Param("branchId") Long branchId, Pageable pageable);

    // Statistics
    @Query("SELECT COUNT(em) FROM EquipmentMaintenance em " +
            "WHERE em.equipment.branch.id = :branchId " +
            "AND em.status = :status")
    long countByBranchAndStatus(@Param("branchId") Long branchId,
            @Param("status") MaintenanceStatus status);

    @Query("SELECT COUNT(em) FROM EquipmentMaintenance em " +
            "WHERE em.equipment.id = :equipmentId " +
            "AND em.status = 'COMPLETED'")
    long countCompletedMaintenanceByEquipment(@Param("equipmentId") Long equipmentId);

    // Recent Maintenance
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.equipment.id = :equipmentId " +
            "ORDER BY em.completedDate DESC")
    List<EquipmentMaintenance> findRecentMaintenanceByEquipment(@Param("equipmentId") Long equipmentId,
            Pageable pageable);
}
