package com.badminton.entity.post;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_likes", uniqueConstraints = @UniqueConstraint(name = "uk_user_post", columnNames = { "user_id",
        "post_id" }), indexes = {
                @Index(name = "idx_post", columnList = "post_id"),
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_created", columnList = "created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_like")
    @Builder.Default
    private Boolean isLike = true; // true = like, false = dislike

    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}
