package com.badminton.repository.equipment;

import com.badminton.entity.equipment.Equipment;
import com.badminton.enums.EquipmentCondition;
import com.badminton.enums.EquipmentStatus;
import com.badminton.enums.EquipmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {

    // Basic Queries
    Optional<Equipment> findByEquipmentCode(String equipmentCode);

    Optional<Equipment> findBySerialNumber(String serialNumber);

    Optional<Equipment> findByBarcode(String barcode);

    Optional<Equipment> findByQrCode(String qrCode);

    boolean existsByEquipmentCode(String equipmentCode);

    boolean existsBySerialNumber(String serialNumber);

    // Branch Queries
    List<Equipment> findByBranchId(Long branchId);

    Page<Equipment> findByBranchId(Long branchId, Pageable pageable);

    List<Equipment> findByBranchIdAndDeletedAtIsNull(Long branchId);

    // Status Queries
    List<Equipment> findByStatus(EquipmentStatus status);

    Page<Equipment> findByStatus(EquipmentStatus status, Pageable pageable);

    List<Equipment> findByBranchIdAndStatus(Long branchId, EquipmentStatus status);

    List<Equipment> findByBranchIdAndStatusAndDeletedAtIsNull(Long branchId, EquipmentStatus status);

    // Condition Queries
    List<Equipment> findByEquipmentCondition(EquipmentCondition condition);

    List<Equipment> findByBranchIdAndEquipmentCondition(Long branchId, EquipmentCondition condition);

    // Type Queries
    List<Equipment> findByEquipmentType(EquipmentType type);

    Page<Equipment> findByEquipmentType(EquipmentType type, Pageable pageable);

    List<Equipment> findByBranchIdAndEquipmentType(Long branchId, EquipmentType type);

    // Availability Queries
    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.isAvailable = true " +
            "AND e.status = 'AVAILABLE' " +
            "AND e.equipmentCondition != 'BROKEN' " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findAvailableEquipmentByBranch(@Param("branchId") Long branchId);

    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.equipmentType = :type " +
            "AND e.isAvailable = true " +
            "AND e.status = 'AVAILABLE' " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findAvailableEquipmentByBranchAndType(@Param("branchId") Long branchId,
            @Param("type") EquipmentType type);

    @Query("SELECT e FROM Equipment e WHERE e.isRentable = true " +
            "AND e.isAvailable = true " +
            "AND e.status = 'AVAILABLE' " +
            "AND e.equipmentCondition != 'BROKEN' " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findAllRentableEquipment();

    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.isRentable = true " +
            "AND e.isAvailable = true " +
            "AND e.status = 'AVAILABLE' " +
            "AND e.deletedAt IS NULL " +
            "ORDER BY e.totalRentals DESC")
    List<Equipment> findPopularRentableEquipment(@Param("branchId") Long branchId, Pageable pageable);

    // Maintenance Queries
    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.nextMaintenanceDate <= :date " +
            "AND e.status != 'MAINTENANCE' " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findEquipmentNeedingMaintenance(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT e FROM Equipment e WHERE e.nextMaintenanceDate <= :date " +
            "AND e.status != 'MAINTENANCE' " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findAllEquipmentNeedingMaintenance(@Param("date") LocalDate date);

    List<Equipment> findByStatusAndDeletedAtIsNull(EquipmentStatus status);

    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.status = 'MAINTENANCE' " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findEquipmentUnderMaintenance(@Param("branchId") Long branchId);

    // Warranty Queries
    @Query("SELECT e FROM Equipment e WHERE e.warrantyExpiryDate BETWEEN :startDate AND :endDate " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findEquipmentWithExpiringWarranty(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM Equipment e WHERE e.warrantyExpiryDate >= :date " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findEquipmentUnderWarranty(@Param("date") LocalDate date);

    // Search Queries
    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND (LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.equipmentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.model) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND e.deletedAt IS NULL")
    Page<Equipment> searchEquipment(@Param("branchId") Long branchId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Price Range Queries
    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.rentalPricePerHour BETWEEN :minPrice AND :maxPrice " +
            "AND e.isRentable = true " +
            "AND e.isAvailable = true " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findByBranchAndRentalPriceRange(@Param("branchId") Long branchId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    // Statistics Queries
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.status = :status " +
            "AND e.deletedAt IS NULL")
    long countByBranchAndStatus(@Param("branchId") Long branchId,
            @Param("status") EquipmentStatus status);

    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.equipmentType = :type " +
            "AND e.deletedAt IS NULL")
    long countByBranchAndType(@Param("branchId") Long branchId,
            @Param("type") EquipmentType type);

    @Query("SELECT e.equipmentType, COUNT(e) FROM Equipment e " +
            "WHERE e.branch.id = :branchId " +
            "AND e.deletedAt IS NULL " +
            "GROUP BY e.equipmentType")
    List<Object[]> countEquipmentByType(@Param("branchId") Long branchId);

    @Query("SELECT e.status, COUNT(e) FROM Equipment e " +
            "WHERE e.branch.id = :branchId " +
            "AND e.deletedAt IS NULL " +
            "GROUP BY e.status")
    List<Object[]> countEquipmentByStatus(@Param("branchId") Long branchId);

    @Query("SELECT SUM(e.currentValue) FROM Equipment e " +
            "WHERE e.branch.id = :branchId " +
            "AND e.deletedAt IS NULL")
    BigDecimal sumTotalValueByBranch(@Param("branchId") Long branchId);

    @Query("SELECT SUM(e.totalRevenue) FROM Equipment e " +
            "WHERE e.branch.id = :branchId " +
            "AND e.deletedAt IS NULL")
    BigDecimal sumTotalRevenueByBranch(@Param("branchId") Long branchId);

    // Top Performers
    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.deletedAt IS NULL " +
            "ORDER BY e.totalRentals DESC")
    List<Equipment> findMostRentedEquipment(@Param("branchId") Long branchId, Pageable pageable);

    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.deletedAt IS NULL " +
            "ORDER BY e.totalRevenue DESC")
    List<Equipment> findHighestRevenueEquipment(@Param("branchId") Long branchId, Pageable pageable);

    // Damage Tracking
    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.damageCount > :minDamageCount " +
            "AND e.deletedAt IS NULL " +
            "ORDER BY e.damageCount DESC")
    List<Equipment> findFrequentlyDamagedEquipment(@Param("branchId") Long branchId,
            @Param("minDamageCount") Integer minDamageCount);

    // Purchase Date Queries
    List<Equipment> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.purchaseDate BETWEEN :startDate AND :endDate " +
            "AND e.deletedAt IS NULL")
    List<Equipment> findByBranchAndPurchaseDateBetween(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Brand Queries
    List<Equipment> findByBrand(String brand);

    List<Equipment> findByBranchIdAndBrand(Long branchId, String brand);

    @Query("SELECT DISTINCT e.brand FROM Equipment e WHERE e.branch.id = :branchId " +
            "AND e.brand IS NOT NULL " +
            "AND e.deletedAt IS NULL " +
            "ORDER BY e.brand")
    List<String> findDistinctBrandsByBranch(@Param("branchId") Long branchId);

    // Location Queries
    List<Equipment> findByStorageLocation(String storageLocation);

    List<Equipment> findByBranchIdAndStorageLocation(Long branchId, String storageLocation);

    @Query("SELECT DISTINCT e.storageLocation FROM Equipment e " +
            "WHERE e.branch.id = :branchId " +
            "AND e.storageLocation IS NOT NULL " +
            "AND e.deletedAt IS NULL " +
            "ORDER BY e.storageLocation")
    List<String> findDistinctStorageLocationsByBranch(@Param("branchId") Long branchId);
}
