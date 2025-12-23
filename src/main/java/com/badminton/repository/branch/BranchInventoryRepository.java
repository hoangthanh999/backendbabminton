package com.badminton.repository.branch;

import com.badminton.entity.branch.BranchInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchInventoryRepository extends JpaRepository<BranchInventory, Long> {

    List<BranchInventory> findByBranchId(Long branchId);

    List<BranchInventory> findByProductId(Long productId);

    Optional<BranchInventory> findByBranchIdAndProductId(Long branchId, Long productId);

    @Query("SELECT bi FROM BranchInventory bi WHERE bi.branch.id = :branchId AND bi.quantity <= bi.minStock")
    List<BranchInventory> findLowStockByBranch(@Param("branchId") Long branchId);

    @Query("SELECT bi FROM BranchInventory bi WHERE bi.branch.id = :branchId AND bi.quantity = 0")
    List<BranchInventory> findOutOfStockByBranch(@Param("branchId") Long branchId);

    @Query("SELECT SUM(bi.quantity) FROM BranchInventory bi WHERE bi.product.id = :productId")
    Integer getTotalStockByProduct(@Param("productId") Long productId);
}
