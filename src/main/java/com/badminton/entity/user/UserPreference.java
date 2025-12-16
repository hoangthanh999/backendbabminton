package com.badminton.entity.user;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.court.Court;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "user_preferences", uniqueConstraints = @UniqueConstraint(name = "uk_user", columnNames = "user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_court_id")
    private Court preferredCourt;

    @Column(name = "preferred_time_start")
    private LocalTime preferredTimeStart;

    @Column(name = "preferred_time_end")
    private LocalTime preferredTimeEnd;

    @Column(name = "notification_email")
    @Builder.Default
    private Boolean notificationEmail = true;

    @Column(name = "notification_sms")
    @Builder.Default
    private Boolean notificationSms = true;

    @Column(name = "notification_push")
    @Builder.Default
    private Boolean notificationPush = true;

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "vi";

    @Column(name = "timezone", length = 50)
    @Builder.Default
    private String timezone = "Asia/Ho_Chi_Minh";
}
