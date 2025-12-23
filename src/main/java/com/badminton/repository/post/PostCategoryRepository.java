package com.badminton.repository.post;

import com.badminton.entity.post.PostCategory;
import com.badminton.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

    // Basic Queries
    Optional<PostCategory> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<PostCategory> findByStatus(CategoryStatus status);

    // Hierarchy Queries
    List<PostCategory> findByParentIsNull();

    List<PostCategory> findByParentId(Long parentId);

    @Query("SELECT c FROM PostCategory c WHERE c.parent IS NULL " +
            "AND c.status = 'ACTIVE' " +
            "ORDER BY c.displayOrder")
    List<PostCategory> findRootCategories();

    @Query("SELECT c FROM PostCategory c WHERE c.parent.id = :parentId " +
            "AND c.status = 'ACTIVE' " +
            "ORDER BY c.displayOrder")
    List<PostCategory> findActiveChildCategories(@Param("parentId") Long parentId);

    @Query("SELECT c FROM PostCategory c WHERE c.parent.id = :parentId " +
            "ORDER BY c.displayOrder")
    List<PostCategory> findChildCategories(@Param("parentId") Long parentId);

    // Level Queries
    List<PostCategory> findByLevel(Integer level);

    @Query("SELECT c FROM PostCategory c WHERE c.level = :level " +
            "AND c.status = 'ACTIVE' " +
            "ORDER BY c.displayOrder")
    List<PostCategory> findActiveCategoriesByLevel(@Param("level") Integer level);

    // Featured Categories
    @Query("SELECT c FROM PostCategory c WHERE c.isFeatured = true " +
            "AND c.status = 'ACTIVE' " +
            "ORDER BY c.displayOrder")
    List<PostCategory> findFeaturedCategories();

    // Menu Categories
    @Query("SELECT c FROM PostCategory c WHERE c.showInMenu = true " +
            "AND c.status = 'ACTIVE' " +
            "ORDER BY c.displayOrder")
    List<PostCategory> findMenuCategories();

    // Search
    @Query("SELECT c FROM PostCategory c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PostCategory> searchCategories(@Param("keyword") String keyword);

    // Statistics
    @Query("SELECT COUNT(c) FROM PostCategory c WHERE c.status = :status")
    long countByStatus(@Param("status") CategoryStatus status);

    @Query("SELECT COUNT(c) FROM PostCategory c WHERE c.parent.id = :parentId")
    long countChildCategories(@Param("parentId") Long parentId);

    // Update Operations
    @Modifying
    @Query("UPDATE PostCategory c SET c.postCount = c.postCount + 1 WHERE c.id = :categoryId")
    void incrementPostCount(@Param("categoryId") Long categoryId);

    @Modifying
    @Query("UPDATE PostCategory c SET c.postCount = c.postCount - 1 WHERE c.id = :categoryId AND c.postCount > 0")
    void decrementPostCount(@Param("categoryId") Long categoryId);

    // Popular Categories
    @Query("SELECT c FROM PostCategory c WHERE c.status = 'ACTIVE' " +
            "ORDER BY c.postCount DESC")
    List<PostCategory> findPopularCategories(org.springframework.data.domain.Pageable pageable);
}
