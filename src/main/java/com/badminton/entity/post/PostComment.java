package com.badminton.entity.post;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post_comments", indexes = {
        @Index(name = "idx_post", columnList = "post_id"),
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_parent", columnList = "parent_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE post_comments SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class PostComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private Set<PostComment> replies = new HashSet<>();

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CommentStatus status = CommentStatus.PENDING;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "reply_count")
    @Builder.Default
    private Integer replyCount = 0;

    @Column(name = "is_edited")
    @Builder.Default
    private Boolean isEdited = false;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Moderation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Helper Methods

    /**
     * Approve comment
     */
    public void approve(User approver) {
        if (status != CommentStatus.PENDING) {
            throw new IllegalStateException("Only pending comments can be approved");
        }

        this.status = CommentStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * Reject comment
     */
    public void reject(String reason) {
        if (status != CommentStatus.PENDING) {
            throw new IllegalStateException("Only pending comments can be rejected");
        }

        this.status = CommentStatus.REJECTED;
        this.rejectionReason = reason;
    }

    /**
     * Mark as spam
     */
    public void markAsSpam() {
        this.status = CommentStatus.SPAM;
    }

    /**
     * Edit comment
     */
    public void edit(String newContent) {
        this.content = newContent;
        this.isEdited = true;
        this.editedAt = LocalDateTime.now();
    }

    /**
     * Add reply
     */
    public void addReply(PostComment reply) {
        replies.add(reply);
        reply.setParent(this);
        this.replyCount++;
    }

    /**
     * Increment like count
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /**
     * Decrement like count
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * Check if is reply
     */
    public boolean isReply() {
        return parent != null;
    }

    /**
     * Get depth level
     */
    public int getDepth() {
        int depth = 0;
        PostComment current = this.parent;

        while (current != null) {
            depth++;
            current = current.getParent();
        }

        return depth;
    }
}
