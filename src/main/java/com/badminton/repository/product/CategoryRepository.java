package com.badminton.repository.product;

import com.badminton.entity.product.Category;
import com.badminton.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByStatus(CategoryStatus status);

    List<Category> findByParentId(Long parentId);

    List<Category> findByParentIsNull();

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.status = 'ACTIVE' ORDER BY c.displayOrder")
    List<Category> findRootCategories();

    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.status = 'ACTIVE' ORDER BY c.displayOrder")
    List<Category> findActiveChildCategories(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.isFeatured = true AND c.status = 'ACTIVE' ORDER BY c.displayOrder")
    List<Category> findFeaturedCategories();
}
