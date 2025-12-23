package com.badminton.entity.post;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_views", indexes = {
        @Index(name = "idx_post", columnList = "post_id"),
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_created", columnList = "created_at"),
        @Index(name = "idx_ip", columnList = "ip_address")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostView extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Null for anonymous users

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "referrer")
    private String referrer;

    @Column(name = "device_type", length = 50)
    private String deviceType; // DESKTOP, MOBILE, TABLET

    @Column(name = "browser", length = 50)
    private String browser;

    @Column(name = "os", length = 50)
    private String os;

    @Column(name = "country", length = 2)
    private String country; // ISO country code

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @Column(name = "scroll_depth_percentage")
    private Integer scrollDepthPercentage;
}
