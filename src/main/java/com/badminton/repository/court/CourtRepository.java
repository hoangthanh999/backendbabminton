package com.badminton.repository.court;

import com.badminton.entity.court.Court;
import com.badminton.enums.CourtStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    List<Court> findByBranchId(Long branchId);

    List<Court> findByBranchIdAndStatus(Long branchId, CourtStatus status);

    List<Court> findByCourtTypeId(Long courtTypeId);

    Optional<Court> findByBranchIdAndCourtNumber(Long branchId, String courtNumber);

    @Query("SELECT c FROM Court c WHERE c.branch.id = :branchId AND c.status = 'AVAILABLE' ORDER BY c.displayOrder")
    List<Court> findAvailableCourtsByBranch(@Param("branchId") Long branchId);

    @Query("SELECT c FROM Court c WHERE c.branch.id = :branchId " +
            "AND c.status = 'AVAILABLE' " +
            "AND c.pricePerHour BETWEEN :minPrice AND :maxPrice")
    List<Court> findAvailableCourtsInPriceRange(@Param("branchId") Long branchId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT COUNT(c) FROM Court c WHERE c.branch.id = :branchId AND c.status = :status")
    long countByBranchAndStatus(@Param("branchId") Long branchId, @Param("status") CourtStatus status);

    @Query("SELECT c FROM Court c WHERE c.branch.id = :branchId AND c.isIndoor = :isIndoor AND c.status = 'AVAILABLE'")
    List<Court> findByBranchAndIndoorType(@Param("branchId") Long branchId, @Param("isIndoor") Boolean isIndoor);
}
