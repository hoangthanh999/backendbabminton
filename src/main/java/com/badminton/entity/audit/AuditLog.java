package com.badminton.entity.audit;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.user.User;
import com.badminton.enums.ActionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_action", columnList = "action_type"),
        @Index(name = "idx_created", columnList = "created_at"),
        @Index(name = "idx_ip", columnList = "ip_address")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "username", length = 100)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private ActionType actionType;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType; // Booking, Order, User, etc.

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "action_description", columnDefinition = "TEXT")
    private String actionDescription;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue; // JSON

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue; // JSON

    @Column(name = "changes", columnDefinition = "TEXT")
    private String changes; // JSON of changed fields

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "request_url", columnDefinition = "TEXT")
    private String requestUrl;

    @Column(name = "request_method", length = 10)
    private String requestMethod; // GET, POST, PUT, DELETE

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "success")
    @Builder.Default
    private Boolean success = true;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    // Helper Methods

    /**
     * Create audit log for entity creation
     */
    public static AuditLog createLog(User user, String entityType, Long entityId,
            String entityName, String newValue) {
        return AuditLog.builder()
                .user(user)
                .username(user != null ? user.getUsername() : "system")
                .actionType(ActionType.CREATE)
                .entityType(entityType)
                .entityId(entityId)
                .entityName(entityName)
                .newValue(newValue)
                .actionDescription("Created " + entityType + ": " + entityName)
                .build();
    }

    /**
     * Create audit log for entity update
     */
    public static AuditLog updateLog(User user, String entityType, Long entityId,
            String entityName, String oldValue, String newValue) {
        return AuditLog.builder()
                .user(user)
                .username(user != null ? user.getUsername() : "system")
                .actionType(ActionType.UPDATE)
                .entityType(entityType)
                .entityId(entityId)
                .entityName(entityName)
                .oldValue(oldValue)
                .newValue(newValue)
                .actionDescription("Updated " + entityType + ": " + entityName)
                .build();
    }

    /**
     * Create audit log for entity deletion
     */
    public static AuditLog deleteLog(User user, String entityType, Long entityId,
            String entityName, String oldValue) {
        return AuditLog.builder()
                .user(user)
                .username(user != null ? user.getUsername() : "system")
                .actionType(ActionType.DELETE)
                .entityType(entityType)
                .entityId(entityId)
                .entityName(entityName)
                .oldValue(oldValue)
                .actionDescription("Deleted " + entityType + ": " + entityName)
                .build();
    }
}
