package com.badminton.entity.post;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.SharePlatform;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_shares", indexes = {
        @Index(name = "idx_post", columnList = "post_id"),
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_platform", columnList = "platform"),
        @Index(name = "idx_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostShare extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 50)
    private SharePlatform platform;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
}
