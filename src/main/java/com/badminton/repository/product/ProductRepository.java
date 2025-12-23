package com.badminton.repository.product;

import com.badminton.entity.product.Product;
import com.badminton.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findBySlug(String slug);

    List<Product> findByCategoryId(Long categoryId);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    List<Product> findByStatus(ProductStatus status);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.deletedAt IS NULL")
    List<Product> findAllActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.displayOrder")
    Page<Product> findAllActiveProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isFeatured = true " +
            "AND p.status = 'ACTIVE' " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.displayOrder")
    List<Product> findFeaturedProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isNew = true " +
            "AND p.status = 'ACTIVE' " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")
    List<Product> findNewProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isBestseller = true " +
            "AND p.status = 'ACTIVE' " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.totalSold DESC")
    List<Product> findBestsellerProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "p.sku LIKE CONCAT('%', :keyword, '%')")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
            "AND p.price BETWEEN :minPrice AND :maxPrice " +
            "AND p.status = 'ACTIVE' " +
            "AND p.deletedAt IS NULL")
    Page<Product> findByCategoryAndPriceRange(@Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.trackInventory = true " +
            "AND p.stock <= p.minStock " +
            "AND p.status = 'ACTIVE'")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.trackInventory = true " +
            "AND p.stock = 0 " +
            "AND p.status = 'ACTIVE'")
    List<Product> findOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = :status")
    long countByStatus(@Param("status") ProductStatus status);
}
