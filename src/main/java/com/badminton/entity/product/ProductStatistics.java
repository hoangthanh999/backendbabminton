package com.badminton.entity.product;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product_statistics", uniqueConstraints = @UniqueConstraint(name = "uk_product", columnNames = "product_id"), indexes = {
        @Index(name = "idx_total_sold", columnList = "total_sold")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatistics extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "total_sold")
    @Builder.Default
    private Integer totalSold = 0;

    @Column(name = "total_revenue", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "last_sold_date")
    private LocalDate lastSoldDate;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "wishlist_count")
    @Builder.Default
    private Long wishlistCount = 0L;

    @Column(name = "cart_count")
    @Builder.Default
    private Long cartCount = 0L;

    @Column(name = "average_rating", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "review_count")
    @Builder.Default
    private Long reviewCount = 0L;

    @Column(name = "return_count")
    @Builder.Default
    private Integer returnCount = 0;

    // Helper Methods

    public void incrementSold(Integer quantity, BigDecimal amount) {
        this.totalSold += quantity;
        this.totalRevenue = this.totalRevenue.add(amount);
        this.lastSoldDate = LocalDate.now();
    }

    public void incrementView() {
        this.viewCount++;
    }

    public void updateRating(BigDecimal newRating) {
        if (reviewCount == 0) {
            this.averageRating = newRating;
        } else {
            BigDecimal totalRating = averageRating.multiply(BigDecimal.valueOf(reviewCount));
            totalRating = totalRating.add(newRating);
            this.averageRating = totalRating.divide(
                    BigDecimal.valueOf(reviewCount + 1),
                    2,
                    java.math.RoundingMode.HALF_UP);
        }
        this.reviewCount++;
    }

    public BigDecimal getConversionRate() {
        if (viewCount == 0)
            return BigDecimal.ZERO;

        return BigDecimal.valueOf(totalSold)
                .divide(BigDecimal.valueOf(viewCount), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}
