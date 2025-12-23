package com.badminton.repository.expense;

import com.badminton.entity.expense.ExpenseBudget;
import com.badminton.enums.BudgetPeriod;
import com.badminton.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseBudgetRepository extends JpaRepository<ExpenseBudget, Long> {

    // Basic Queries
    List<ExpenseBudget> findByBranchId(Long branchId);

    List<ExpenseBudget> findByCategory(ExpenseCategory category);

    List<ExpenseBudget> findByBranchIdAndCategory(Long branchId, ExpenseCategory category);

    List<ExpenseBudget> findByPeriod(BudgetPeriod period);

    // Active Budgets
    @Query("SELECT eb FROM ExpenseBudget eb WHERE eb.branch.id = :branchId " +
            "AND eb.periodStart <= :date " +
            "AND eb.periodEnd >= :date")
    List<ExpenseBudget> findActiveBudgetsByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT eb FROM ExpenseBudget eb WHERE eb.branch.id = :branchId " +
            "AND eb.category = :category " +
            "AND eb.periodStart <= :date " +
            "AND eb.periodEnd >= :date")
    Optional<ExpenseBudget> findActiveBudgetByBranchAndCategory(@Param("branchId") Long branchId,
            @Param("category") ExpenseCategory category,
            @Param("date") LocalDate date);

    // Period Queries
    @Query("SELECT eb FROM ExpenseBudget eb WHERE eb.branch.id = :branchId " +
            "AND eb.periodStart >= :startDate " +
            "AND eb.periodEnd <= :endDate")
    List<ExpenseBudget> findByBranchAndPeriodRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Over Budget
    @Query("SELECT eb FROM ExpenseBudget eb WHERE eb.branch.id = :branchId " +
            "AND eb.spentAmount > eb.allocatedAmount")
    List<ExpenseBudget> findOverBudgetByBranch(@Param("branchId") Long branchId);

    @Query("SELECT eb FROM ExpenseBudget eb WHERE eb.spentAmount > eb.allocatedAmount")
    List<ExpenseBudget> findAllOverBudget();

    // Near Budget Limit
    @Query("SELECT eb FROM ExpenseBudget eb WHERE eb.branch.id = :branchId " +
            "AND eb.spentAmount >= (eb.allocatedAmount * :threshold / 100) " +
            "AND eb.spentAmount <= eb.allocatedAmount " +
            "AND eb.periodStart <= :date " +
            "AND eb.periodEnd >= :date")
    List<ExpenseBudget> findNearBudgetLimit(@Param("branchId") Long branchId,
            @Param("threshold") Integer threshold,
            @Param("date") LocalDate date);

    // Statistics
    @Query("SELECT SUM(eb.allocatedAmount) FROM ExpenseBudget eb " +
            "WHERE eb.branch.id = :branchId " +
            "AND eb.periodStart <= :date " +
            "AND eb.periodEnd >= :date")
    BigDecimal sumTotalBudgetByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT SUM(eb.spentAmount) FROM ExpenseBudget eb " +
            "WHERE eb.branch.id = :branchId " +
            "AND eb.periodStart <= :date " +
            "AND eb.periodEnd >= :date")
    BigDecimal sumTotalSpentByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    @Query("SELECT eb.category, eb.allocatedAmount, eb.spentAmount, eb.remainingAmount " +
            "FROM ExpenseBudget eb " +
            "WHERE eb.branch.id = :branchId " +
            "AND eb.periodStart <= :date " +
            "AND eb.periodEnd >= :date " +
            "ORDER BY eb.category")
    List<Object[]> getBudgetSummaryByBranch(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);

    // Utilization Analysis
    @Query("SELECT eb FROM ExpenseBudget eb WHERE eb.branch.id = :branchId " +
            "AND eb.periodStart <= :date " +
            "AND eb.periodEnd >= :date " +
            "ORDER BY (eb.spentAmount / eb.allocatedAmount) DESC")
    List<ExpenseBudget> findBudgetsByUtilizationRate(@Param("branchId") Long branchId,
            @Param("date") LocalDate date);
}
