package com.badminton.entity.product;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_reviews", indexes = {
        @Index(name = "idx_product", columnList = "product_id"),
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_rating", columnList = "rating"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "is_verified_purchase")
    @Builder.Default
    private Boolean isVerifiedPurchase = false;

    @Column(name = "helpful_count")
    @Builder.Default
    private Integer helpfulCount = 0;

    @Column(name = "images", columnDefinition = "JSON")
    private String images;

    @Column(name = "admin_response", columnDefinition = "TEXT")
    private String adminResponse;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    // Helper Methods
    public void approve() {
        this.status = ReviewStatus.APPROVED;
    }

    public void reject() {
        this.status = ReviewStatus.REJECTED;
    }

    public void incrementHelpful() {
        this.helpfulCount++;
    }
}
