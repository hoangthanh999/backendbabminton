package com.badminton.repository.branch;

import com.badminton.entity.branch.Branch;
import com.badminton.enums.BranchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByCode(String code);

    Optional<Branch> findBySlug(String slug);

    boolean existsByCode(String code);

    boolean existsBySlug(String slug);

    List<Branch> findByStatus(BranchStatus status);

    List<Branch> findByIsActiveTrue();

    List<Branch> findByCity(String city);

    List<Branch> findByCityAndStatus(String city, BranchStatus status);

    @Query("SELECT b FROM Branch b WHERE b.isActive = true AND b.status = 'ACTIVE' ORDER BY b.displayOrder")
    List<Branch> findAllActiveBranches();

    @Query("SELECT b FROM Branch b WHERE " +
            "LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Branch> searchBranches(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Branch b WHERE b.status = :status")
    long countByStatus(@Param("status") BranchStatus status);

    @Query("SELECT b FROM Branch b WHERE b.latitude BETWEEN :minLat AND :maxLat " +
            "AND b.longitude BETWEEN :minLng AND :maxLng")
    List<Branch> findBranchesInArea(@Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng);
}
