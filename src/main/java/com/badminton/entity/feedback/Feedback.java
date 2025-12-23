package com.badminton.entity.feedback;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.booking.Booking;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.order.Order;
import com.badminton.entity.user.User;
import com.badminton.enums.FeedbackCategory;
import com.badminton.enums.FeedbackStatus;
import com.badminton.enums.FeedbackType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks", indexes = {
        @Index(name = "idx_branch_status", columnList = "branch_id, status"),
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_booking", columnList = "booking_id"),
        @Index(name = "idx_order", columnList = "order_id"),
        @Index(name = "idx_rating", columnList = "rating"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback extends AuditableEntity {

    // References
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // Feedback Details
    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false, length = 30)
    private FeedbackType feedbackType;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private FeedbackCategory category;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5 stars

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private FeedbackStatus status = FeedbackStatus.PENDING;

    // Detailed Ratings
    @Column(name = "service_rating")
    private Integer serviceRating; // 1-5

    @Column(name = "facility_rating")
    private Integer facilityRating; // 1-5

    @Column(name = "cleanliness_rating")
    private Integer cleanlinessRating; // 1-5

    @Column(name = "value_rating")
    private Integer valueRating; // 1-5

    @Column(name = "staff_rating")
    private Integer staffRating; // 1-5

    // Media
    @Column(name = "images", columnDefinition = "JSON")
    private String images; // JSON array of image URLs

    @Column(name = "video_url")
    private String videoUrl;

    // Response
    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responded_by")
    private User respondedBy;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    // Verification
    @Column(name = "is_verified_customer")
    @Builder.Default
    private Boolean isVerifiedCustomer = false;

    @Column(name = "is_anonymous")
    @Builder.Default
    private Boolean isAnonymous = false;

    // Moderation
    @Column(name = "is_published")
    @Builder.Default
    private Boolean isPublished = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderated_by")
    private User moderatedBy;

    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;

    @Column(name = "moderation_notes", columnDefinition = "TEXT")
    private String moderationNotes;

    // Helpfulness
    @Column(name = "helpful_count")
    @Builder.Default
    private Integer helpfulCount = 0;

    @Column(name = "not_helpful_count")
    @Builder.Default
    private Integer notHelpfulCount = 0;

    // Additional Info
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;

    // Helper Methods

    /**
     * Approve feedback
     */
    public void approve(User moderator) {
        if (status != FeedbackStatus.PENDING) {
            throw new IllegalStateException("Only pending feedback can be approved");
        }

        this.status = FeedbackStatus.APPROVED;
        this.moderatedBy = moderator;
        this.moderatedAt = LocalDateTime.now();
        this.isPublished = true;
        this.publishedAt = LocalDateTime.now();
    }

    /**
     * Reject feedback
     */
    public void reject(User moderator, String reason) {
        if (status != FeedbackStatus.PENDING) {
            throw new IllegalStateException("Only pending feedback can be rejected");
        }

        this.status = FeedbackStatus.REJECTED;
        this.moderatedBy = moderator;
        this.moderatedAt = LocalDateTime.now();
        this.moderationNotes = reason;
    }

    /**
     * Add response
     */
    public void addResponse(String response, User responder) {
        this.response = response;
        this.respondedBy = responder;
        this.respondedAt = LocalDateTime.now();
        this.status = FeedbackStatus.RESPONDED;
    }

    /**
     * Mark as resolved
     */
    public void resolve() {
        this.status = FeedbackStatus.RESOLVED;
    }

    /**
     * Increment helpful count
     */
    public void markAsHelpful() {
        this.helpfulCount++;
    }

    /**
     * Increment not helpful count
     */
    public void markAsNotHelpful() {
        this.notHelpfulCount++;
    }

    /**
     * Calculate average rating
     */
    public Double getAverageDetailedRating() {
        int count = 0;
        int total = 0;

        if (serviceRating != null) {
            total += serviceRating;
            count++;
        }
        if (facilityRating != null) {
            total += facilityRating;
            count++;
        }
        if (cleanlinessRating != null) {
            total += cleanlinessRating;
            count++;
        }
        if (valueRating != null) {
            total += valueRating;
            count++;
        }
        if (staffRating != null) {
            total += staffRating;
            count++;
        }

        return count > 0 ? (double) total / count : null;
    }

    /**
     * Get helpfulness ratio
     */
    public Double getHelpfulnessRatio() {
        int total = helpfulCount + notHelpfulCount;
        if (total == 0)
            return null;

        return (double) helpfulCount / total * 100;
    }

    /**
     * Check if needs response
     */
    public boolean needsResponse() {
        return status == FeedbackStatus.APPROVED && response == null;
    }
}
