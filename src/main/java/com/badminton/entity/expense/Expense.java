package com.badminton.entity.expense;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.entity.user.User;
import com.badminton.enums.ExpenseCategory;
import com.badminton.enums.ExpenseStatus;
import com.badminton.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses", indexes = {
        @Index(name = "idx_branch_date", columnList = "branch_id, expense_date"),
        @Index(name = "idx_category_date", columnList = "category, expense_date"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_by", columnList = "created_by"),
        @Index(name = "idx_approved_by", columnList = "approved_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "expense_code", unique = true, length = 50)
    private String expenseCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ExpenseCategory category;

    @Column(name = "subcategory", length = 100)
    private String subcategory;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ExpenseStatus status = ExpenseStatus.PENDING;

    // Vendor Info
    @Column(name = "vendor_name", length = 200)
    private String vendorName;

    @Column(name = "vendor_contact", length = 100)
    private String vendorContact;

    @Column(name = "vendor_address", columnDefinition = "TEXT")
    private String vendorAddress;

    @Column(name = "vendor_tax_code", length = 50)
    private String vendorTaxCode;

    // Invoice Info
    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "receipt_number", length = 100)
    private String receiptNumber;

    // Approval Workflow
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejected_by")
    private User rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Payment Info
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "payment_notes", columnDefinition = "TEXT")
    private String paymentNotes;

    // Attachments
    @Column(name = "attachments", columnDefinition = "JSON")
    private String attachments; // JSON array of file URLs

    @Column(name = "receipt_image")
    private String receiptImage;

    // Recurring
    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    @Column(name = "recurring_frequency", length = 20)
    private String recurringFrequency; // DAILY, WEEKLY, MONTHLY, YEARLY

    @Column(name = "recurring_until")
    private LocalDate recurringUntil;

    @Column(name = "parent_expense_id")
    private Long parentExpenseId;

    // Budget
    @Column(name = "budget_id")
    private Long budgetId;

    @Column(name = "is_budgeted")
    @Builder.Default
    private Boolean isBudgeted = false;

    // Reimbursement
    @Column(name = "is_reimbursable")
    @Builder.Default
    private Boolean isReimbursable = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reimbursed_to")
    private User reimbursedTo;

    @Column(name = "reimbursed_at")
    private LocalDateTime reimbursedAt;

    // Additional Info
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags; // JSON array

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    // Lifecycle
    @PrePersist
    public void prePersist() {
        if (expenseCode == null) {
            expenseCode = generateExpenseCode();
        }

        calculateTotalAmount();
    }

    @PreUpdate
    public void preUpdate() {
        calculateTotalAmount();
    }

    // Helper Methods

    /**
     * Generate expense code
     */
    private String generateExpenseCode() {
        return String.format("EXP%s%06d",
                LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                (int) (Math.random() * 1000000));
    }

    /**
     * Calculate total amount
     */
    public void calculateTotalAmount() {
        if (amount != null) {
            BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
            this.totalAmount = amount.add(tax);
        }
    }

    /**
     * Approve expense
     */
    public void approve(User approver) {
        if (status != ExpenseStatus.PENDING) {
            throw new IllegalStateException("Only pending expenses can be approved");
        }

        this.status = ExpenseStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * Reject expense
     */
    public void reject(User rejector, String reason) {
        if (status != ExpenseStatus.PENDING) {
            throw new IllegalStateException("Only pending expenses can be rejected");
        }

        this.status = ExpenseStatus.REJECTED;
        this.rejectedBy = rejector;
        this.rejectedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    /**
     * Mark as paid
     */
    public void markAsPaid(String paymentReference) {
        if (status != ExpenseStatus.APPROVED) {
            throw new IllegalStateException("Only approved expenses can be marked as paid");
        }

        this.status = ExpenseStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.paymentReference = paymentReference;
    }

    /**
     * Reimburse
     */
    public void reimburse(User user) {
        if (!isReimbursable) {
            throw new IllegalStateException("This expense is not reimbursable");
        }

        if (status != ExpenseStatus.PAID) {
            throw new IllegalStateException("Only paid expenses can be reimbursed");
        }

        this.reimbursedTo = user;
        this.reimbursedAt = LocalDateTime.now();
    }

    /**
     * Check if overdue for payment
     */
    public boolean isOverdueForPayment() {
        return status == ExpenseStatus.APPROVED &&
                expenseDate.plusDays(30).isBefore(LocalDate.now());
    }

    /**
     * Get age in days
     */
    public Long getAgeInDays() {
        return java.time.temporal.ChronoUnit.DAYS
                .between(expenseDate, LocalDate.now());
    }

    /**
     * Check if requires approval
     */
    public boolean requiresApproval() {
        // Expenses over 5,000,000 VND require approval
        return totalAmount.compareTo(new BigDecimal("5000000")) > 0;
    }
}
