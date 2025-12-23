package com.badminton.repository.equipment;

import com.badminton.entity.equipment.EquipmentAssignment;
import com.badminton.enums.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentAssignmentRepository extends JpaRepository<EquipmentAssignment, Long> {

    // Basic Queries
    List<EquipmentAssignment> findByEquipmentId(Long equipmentId);

    Page<EquipmentAssignment> findByEquipmentId(Long equipmentId, Pageable pageable);

    List<EquipmentAssignment> findByUserId(Long userId);

    Page<EquipmentAssignment> findByUserId(Long userId, Pageable pageable);

    // Status Queries
    List<EquipmentAssignment> findByStatus(AssignmentStatus status);

    List<EquipmentAssignment> findByEquipmentIdAndStatus(Long equipmentId, AssignmentStatus status);

    List<EquipmentAssignment> findByUserIdAndStatus(Long userId, AssignmentStatus status);

    // Active Assignments
    @Query("SELECT ea FROM EquipmentAssignment ea WHERE ea.equipment.id = :equipmentId " +
            "AND ea.status = 'ACTIVE'")
    List<EquipmentAssignment> findActiveAssignmentsByEquipment(@Param("equipmentId") Long equipmentId);

    @Query("SELECT ea FROM EquipmentAssignment ea WHERE ea.user.id = :userId " +
            "AND ea.status = 'ACTIVE'")
    List<EquipmentAssignment> findActiveAssignmentsByUser(@Param("userId") Long userId);

    @Query("SELECT ea FROM EquipmentAssignment ea WHERE ea.equipment.branch.id = :branchId " +
            "AND ea.status = 'ACTIVE'")
    List<EquipmentAssignment> findActiveAssignmentsByBranch(@Param("branchId") Long branchId);

    // Current Assignment
    @Query("SELECT ea FROM EquipmentAssignment ea WHERE ea.equipment.id = :equipmentId " +
            "AND ea.status = 'ACTIVE' " +
            "ORDER BY ea.assignedDate DESC")
    Optional<EquipmentAssignment> findCurrentAssignment(@Param("equipmentId") Long equipmentId);

    // Overdue Assignments
    @Query("SELECT ea FROM EquipmentAssignment ea WHERE ea.status = 'ACTIVE' " +
            "AND ea.expectedReturnDate < :date")
    List<EquipmentAssignment> findOverdueAssignments(@Param("date") LocalDate date);

    @Query("SELECT ea FROM EquipmentAssignment ea WHERE ea.equipment.branch.id = :branchId " +
            "AND ea.status = 'ACTIVE' " +
            "AND ea.expectedReturnDate < :date")
    List<EquipmentAssignment> findOverdueAssignmentsByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    // Date Range Queries
    List<EquipmentAssignment> findByAssignedDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT ea FROM EquipmentAssignment ea WHERE ea.equipment.branch.id = :branchId " +
            "AND ea.assignedDate BETWEEN :startDate AND :endDate")
    List<EquipmentAssignment> findByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Damage Tracking
    @Query("SELECT ea FROM EquipmentAssignment ea WHERE ea.isDamaged = true " +
            "AND ea.equipment.branch.id = :branchId")
    List<EquipmentAssignment> findDamagedAssignmentsByBranch(@Param("branchId") Long branchId);

    List<EquipmentAssignment> findByIsDamagedTrue();

    // Statistics
    @Query("SELECT COUNT(ea) FROM EquipmentAssignment ea WHERE ea.equipment.branch.id = :branchId " +
            "AND ea.status = :status")
    long countByBranchAndStatus(@Param("branchId") Long branchId,
            @Param("status") AssignmentStatus status);

    @Query("SELECT COUNT(ea) FROM EquipmentAssignment ea WHERE ea.user.id = :userId " +
            "AND ea.status = 'ACTIVE'")
    long countActiveAssignmentsByUser(@Param("userId") Long userId);

    @Query("SELECT ea.user, COUNT(ea) FROM EquipmentAssignment ea " +
            "WHERE ea.equipment.branch.id = :branchId " +
            "AND ea.assignedDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ea.user " +
            "ORDER BY COUNT(ea) DESC")
    List<Object[]> findTopUsersByAssignmentCount(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
