package com.badminton.entity.product;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.branch.BranchInventory;
import com.badminton.entity.order.OrderItem;
import com.badminton.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(name = "uk_slug", columnNames = "slug"), indexes = {
        @Index(name = "idx_category_status_stock", columnList = "category_id, status, stock"),
        @Index(name = "idx_products_complex", columnList = "category_id, status, stock, price"),
        @Index(name = "idx_sku", columnList = "sku"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE products SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Product extends AuditableEntity {

    // Basic Info
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "sku", unique = true, length = 50)
    private String sku; // Stock Keeping Unit

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    // Pricing
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice; // Price before discount

    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice; // Purchase cost

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    // Inventory
    @Column(name = "stock")
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "min_stock")
    @Builder.Default
    private Integer minStock = 10;

    @Column(name = "max_stock")
    @Builder.Default
    private Integer maxStock = 1000;

    @Column(name = "reorder_level")
    @Builder.Default
    private Integer reorderLevel = 20;

    @Column(name = "track_inventory")
    @Builder.Default
    private Boolean trackInventory = true;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_new")
    @Builder.Default
    private Boolean isNew = false;

    @Column(name = "is_bestseller")
    @Builder.Default
    private Boolean isBestseller = false;

    // Product Details
    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "warranty_period")
    private Integer warrantyPeriod; // in months

    @Column(name = "warranty_info", columnDefinition = "TEXT")
    private String warrantyInfo;

    // Physical Properties
    @Column(name = "weight", precision = 8, scale = 2)
    private BigDecimal weight; // in kg

    @Column(name = "length", precision = 8, scale = 2)
    private BigDecimal length; // in cm

    @Column(name = "width", precision = 8, scale = 2)
    private BigDecimal width; // in cm

    @Column(name = "height", precision = 8, scale = 2)
    private BigDecimal height; // in cm

    // Images
    @Column(name = "main_image")
    private String mainImage;

    @Column(name = "images", columnDefinition = "JSON")
    private String images; // JSON array of image URLs

    @Column(name = "thumbnail")
    private String thumbnail;

    // SEO
    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    // Specifications
    @Column(name = "specifications", columnDefinition = "JSON")
    private String specifications; // JSON object

    @Column(name = "attributes", columnDefinition = "JSON")
    private String attributes; // JSON object: {"color": "red", "size": "XL"}

    // Sales Info
    @Column(name = "min_order_quantity")
    @Builder.Default
    private Integer minOrderQuantity = 1;

    @Column(name = "max_order_quantity")
    private Integer maxOrderQuantity;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "is_taxable")
    @Builder.Default
    private Boolean isTaxable = false;

    // Display
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    // Timestamps
    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Column(name = "available_to")
    private LocalDateTime availableTo;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Calculated Fields
    @Formula("(SELECT COUNT(*) FROM order_items oi WHERE oi.item_id = id AND oi.item_type = 'PRODUCT')")
    private Long totalSold;

    @Formula("(SELECT AVG(pr.rating) FROM product_reviews pr WHERE pr.product_id = id)")
    private Double averageRating;

    @Formula("(SELECT COUNT(*) FROM product_reviews pr WHERE pr.product_id = id)")
    private Long reviewCount;

    // Relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BranchInventory> branchInventories = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductReview> reviews = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductImage> productImages = new HashSet<>();

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductStatistics statistics;

    @ManyToMany
    @JoinTable(name = "product_related", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "related_product_id"))
    @Builder.Default
    private Set<Product> relatedProducts = new HashSet<>();

    // Lifecycle Callbacks
    @PrePersist
    public void prePersist() {
        if (slug == null && name != null) {
            slug = generateSlug(name);
        }

        if (sku == null) {
            sku = generateSKU();
        }

        if (originalPrice == null) {
            originalPrice = price;
        }

        updateDiscountPercentage();
    }

    @PreUpdate
    public void preUpdate() {
        updateDiscountPercentage();
        updateStatus();
    }

    // Helper Methods

    /**
     * Generate slug from name
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    /**
     * Generate SKU
     */
    private String generateSKU() {
        return String.format("PRD%s%06d",
                java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                (int) (Math.random() * 1000000));
    }

    /**
     * Update discount percentage
     */
    private void updateDiscountPercentage() {
        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = originalPrice.subtract(price);
            this.discountPercentage = discount
                    .divide(originalPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
    }

    /**
     * Update status based on stock
     */
    private void updateStatus() {
        if (trackInventory && stock != null && stock == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else if (status == ProductStatus.OUT_OF_STOCK && stock != null && stock > 0) {
            this.status = ProductStatus.ACTIVE;
        }
    }

    /**
     * Check if product is available
     */
    public boolean isAvailable() {
        if (status != ProductStatus.ACTIVE) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (availableFrom != null && now.isBefore(availableFrom)) {
            return false;
        }

        if (availableTo != null && now.isAfter(availableTo)) {
            return false;
        }

        if (trackInventory && (stock == null || stock <= 0)) {
            return false;
        }

        return true;
    }

    /**
     * Check if in stock
     */
    public boolean isInStock() {
        if (!trackInventory) {
            return true;
        }
        return stock != null && stock > 0;
    }

    /**
     * Check if low stock
     */
    public boolean isLowStock() {
        if (!trackInventory) {
            return false;
        }
        return stock != null && stock <= minStock;
    }

    /**
     * Check if needs reorder
     */
    public boolean needsReorder() {
        if (!trackInventory) {
            return false;
        }
        return stock != null && stock <= reorderLevel;
    }

    /**
     * Add stock
     */
    public void addStock(Integer quantity) {
        if (trackInventory) {
            this.stock = (this.stock != null ? this.stock : 0) + quantity;
            updateStatus();
        }
    }

    /**
     * Remove stock
     */
    public void removeStock(Integer quantity) {
        if (trackInventory) {
            if (this.stock == null || this.stock < quantity) {
                throw new IllegalArgumentException("Insufficient stock");
            }
            this.stock -= quantity;
            updateStatus();
        }
    }

    /**
     * Get final price (after discount)
     */
    public BigDecimal getFinalPrice() {
        return price;
    }

    /**
     * Get discount amount
     */
    public BigDecimal getDiscountAmount() {
        if (originalPrice != null) {
            return originalPrice.subtract(price);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calculate profit margin
     */
    public BigDecimal getProfitMargin() {
        if (costPrice != null && costPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profit = price.subtract(costPrice);
            return profit.divide(costPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get tax amount
     */
    public BigDecimal getTaxAmount() {
        if (isTaxable && taxRate != null) {
            return price.multiply(taxRate).divide(
                    new BigDecimal("100"),
                    2,
                    java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get price including tax
     */
    public BigDecimal getPriceWithTax() {
        return price.add(getTaxAmount());
    }

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount != null ? this.viewCount : 0L) + 1;
    }

    /**
     * Check if on sale
     */
    public boolean isOnSale() {
        return originalPrice != null &&
                originalPrice.compareTo(price) > 0 &&
                discountPercentage.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Get stock percentage
     */
    public BigDecimal getStockPercentage() {
        if (!trackInventory || maxStock == null || maxStock == 0) {
            return new BigDecimal("100");
        }

        int currentStock = stock != null ? stock : 0;
        return BigDecimal.valueOf(currentStock)
                .divide(BigDecimal.valueOf(maxStock), 2, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Add related product
     */
    public void addRelatedProduct(Product product) {
        relatedProducts.add(product);
        product.getRelatedProducts().add(this);
    }

    /**
     * Remove related product
     */
    public void removeRelatedProduct(Product product) {
        relatedProducts.remove(product);
        product.getRelatedProducts().remove(this);
    }
}
