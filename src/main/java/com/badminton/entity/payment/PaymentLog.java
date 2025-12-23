package com.badminton.entity.payment;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_logs", indexes = {
        @Index(name = "idx_payment", columnList = "payment_id"),
        @Index(name = "idx_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "response_code", length = 50)
    private String responseCode;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    @Column(name = "request_data", columnDefinition = "TEXT")
    private String requestData; // JSON

    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData; // JSON

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "log_level", length = 20)
    @Builder.Default
    private String logLevel = "INFO"; // INFO, WARNING, ERROR

    // Helper Methods
    public boolean isError() {
        return "ERROR".equals(logLevel);
    }

    public boolean isSuccess() {
        return "00".equals(responseCode) || "SUCCESS".equals(responseCode);
    }
}
