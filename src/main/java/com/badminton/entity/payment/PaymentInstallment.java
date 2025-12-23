package com.badminton.entity.payment;

import com.badminton.entity.base.BaseEntity;
import com.badminton.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_installments", indexes = {
        @Index(name = "idx_payment", columnList = "payment_id"),
        @Index(name = "idx_due_date", columnList = "due_date"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInstallment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "total_installments", nullable = false)
    private Integer totalInstallments;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "late_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal lateFee = BigDecimal.ZERO;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Helper Methods

    /**
     * Check if overdue
     */
    public boolean isOverdue() {
        return status == PaymentStatus.PENDING
                && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Get days overdue
     */
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    /**
     * Calculate late fee
     */
    public void calculateLateFee(BigDecimal dailyRate) {
        if (isOverdue()) {
            long days = getDaysOverdue();
            this.lateFee = amount.multiply(dailyRate)
                    .multiply(BigDecimal.valueOf(days))
                    .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    /**
     * Mark as paid
     */
    public void markAsPaid(BigDecimal amount, String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.paidAmount = amount;
        this.paidAt = LocalDateTime.now();
        this.transactionId = transactionId;
    }

    /**
     * Get total amount due (including late fee)
     */
    public BigDecimal getTotalAmountDue() {
        return amount.add(lateFee);
    }

    /**
     * Get remaining amount
     */
    public BigDecimal getRemainingAmount() {
        return getTotalAmountDue().subtract(paidAmount);
    }
}
