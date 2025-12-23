package com.badminton.entity.payment;

import com.badminton.entity.base.BaseEntity;
import com.badminton.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions", indexes = {
        @Index(name = "idx_payment", columnList = "payment_id"),
        @Index(name = "idx_type", columnList = "transaction_type"),
        @Index(name = "idx_created", columnList = "created_at"),
        @Index(name = "idx_reference", columnList = "reference_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType; // CHARGE, REFUND, CHARGEBACK

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "reference_number", unique = true)
    private String referenceNumber;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "response_code", length = 50)
    private String responseCode;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    // Helper Methods
    public boolean isSuccessful() {
        return "00".equals(responseCode) || "SUCCESS".equals(responseCode);
    }
}
