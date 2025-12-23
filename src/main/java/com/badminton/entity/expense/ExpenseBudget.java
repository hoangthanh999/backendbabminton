package com.badminton.entity.expense;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.branch.Branch;
import com.badminton.enums.BudgetPeriod;
import com.badminton.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense_budgets", indexes = {
        @Index(name = "idx_branch_period", columnList = "branch_id, period_start, period_end"),
        @Index(name = "idx_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseBudget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false, length = 20)
    private BudgetPeriod period;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "allocated_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal allocatedAmount;

    @Column(name = "spent_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(name = "remaining_amount", precision = 12, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Lifecycle
    @PrePersist
    @PreUpdate
    public void calculateRemaining() {
        this.remainingAmount = allocatedAmount.subtract(spentAmount);
    }

    // Helper Methods

    /**
     * Add expense to budget
     */
    public void addExpense(BigDecimal amount) {
        this.spentAmount = this.spentAmount.add(amount);
        calculateRemaining();
    }

    /**
     * Check if over budget
     */
    public boolean isOverBudget() {
        return spentAmount.compareTo(allocatedAmount) > 0;
    }

    /**
     * Get utilization percentage
     */
    public BigDecimal getUtilizationPercentage() {
        if (allocatedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return spentAmount
                .divide(allocatedAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Check if budget is active
     */
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(periodStart) && !now.isAfter(periodEnd);
    }

    /**
     * Get days remaining
     */
    public Long getDaysRemaining() {
        if (LocalDate.now().isAfter(periodEnd)) {
            return 0L;
        }
        return java.time.temporal.ChronoUnit.DAYS
                .between(LocalDate.now(), periodEnd);
    }
}
