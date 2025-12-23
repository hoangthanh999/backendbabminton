package com.badminton.repository.expense;

import com.badminton.entity.expense.Expense;
import com.badminton.enums.ExpenseCategory;
import com.badminton.enums.ExpenseStatus;
import com.badminton.enums.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    // Basic Queries
    Optional<Expense> findByExpenseCode(String expenseCode);

    List<Expense> findByBranchId(Long branchId);

    Page<Expense> findByBranchId(Long branchId, Pageable pageable);

    // Status Queries
    List<Expense> findByStatus(ExpenseStatus status);

    Page<Expense> findByStatus(ExpenseStatus status, Pageable pageable);

    List<Expense> findByBranchIdAndStatus(Long branchId, ExpenseStatus status);

    Page<Expense> findByBranchIdAndStatus(Long branchId, ExpenseStatus status, Pageable pageable);

    // Category Queries
    List<Expense> findByCategory(ExpenseCategory category);

    Page<Expense> findByCategory(ExpenseCategory category, Pageable pageable);

    List<Expense> findByBranchIdAndCategory(Long branchId, ExpenseCategory category);

    @Query("SELECT e FROM Expense e WHERE e.branch.id = :branchId " +
            "AND e.category = :category " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate")
    List<Expense> findByBranchCategoryAndDateRange(@Param("branchId") Long branchId,
            @Param("category") ExpenseCategory category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Date Queries
    List<Expense> findByExpenseDate(LocalDate date);

    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> findByBranchIdAndExpenseDateBetween(Long branchId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM Expense e WHERE e.branch.id = :branchId " +
            "AND YEAR(e.expenseDate) = :year " +
            "AND MONTH(e.expenseDate) = :month")
    List<Expense> findByBranchAndYearMonth(@Param("branchId") Long branchId,
            @Param("year") Integer year,
            @Param("month") Integer month);

    // Payment Method Queries
    List<Expense> findByPaymentMethod(PaymentMethod paymentMethod);

    List<Expense> findByBranchIdAndPaymentMethod(Long branchId, PaymentMethod paymentMethod);

    // Approval Queries
    @Query("SELECT e FROM Expense e WHERE e.branch.id = :branchId " +
            "AND e.status = 'PENDING' " +
            "ORDER BY e.expenseDate DESC")
    List<Expense> findPendingExpensesByBranch(@Param("branchId") Long branchId);

    @Query("SELECT e FROM Expense e WHERE e.status = 'PENDING' " +
            "ORDER BY e.expenseDate DESC")
    List<Expense> findAllPendingExpenses();

    List<Expense> findByApprovedById(Long userId);

    // Payment Status Queries
    @Query("SELECT e FROM Expense e WHERE e.branch.id = :branchId " +
            "AND e.status = 'APPROVED' " +
            "AND e.paidAt IS NULL")
    List<Expense> findUnpaidExpensesByBranch(@Param("branchId") Long branchId);

    @Query("SELECT e FROM Expense e WHERE e.status = 'APPROVED' " +
            "AND e.paidAt IS NULL " +
            "AND e.expenseDate < :date")
    List<Expense> findOverdueUnpaidExpenses(@Param("date") LocalDate date);

    // Recurring Expenses
    List<Expense> findByIsRecurringTrue();

    List<Expense> findByBranchIdAndIsRecurringTrue(Long branchId);

    List<Expense> findByParentExpenseId(Long parentExpenseId);

    // Reimbursement Queries
    List<Expense> findByIsReimbursableTrue();

    List<Expense> findByReimbursedToId(Long userId);

    @Query("SELECT e FROM Expense e WHERE e.isReimbursable = true " +
            "AND e.reimbursedAt IS NULL " +
            "AND e.status = 'PAID'")
    List<Expense> findUnreimbursedExpenses();

    // Vendor Queries
    List<Expense> findByVendorName(String vendorName);

    @Query("SELECT DISTINCT e.vendorName FROM Expense e " +
            "WHERE e.branch.id = :branchId " +
            "AND e.vendorName IS NOT NULL " +
            "ORDER BY e.vendorName")
    List<String> findDistinctVendorsByBranch(@Param("branchId") Long branchId);

    @Query("SELECT e.vendorName, COUNT(e), SUM(e.totalAmount) FROM Expense e " +
            "WHERE e.branch.id = :branchId " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate " +
            "GROUP BY e.vendorName " +
            "ORDER BY SUM(e.totalAmount) DESC")
    List<Object[]> getTopVendorsBySpending(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // Search Queries
    @Query("SELECT e FROM Expense e WHERE e.branch.id = :branchId " +
            "AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.expenseCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.vendorName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Expense> searchExpenses(@Param("branchId") Long branchId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Statistics Queries
    @Query("SELECT SUM(e.totalAmount) FROM Expense e WHERE e.branch.id = :branchId " +
            "AND e.status = 'PAID' " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalExpensesByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(e.totalAmount) FROM Expense e WHERE e.branch.id = :branchId " +
            "AND e.category = :category " +
            "AND e.status = 'PAID' " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpensesByCategoryAndDateRange(@Param("branchId") Long branchId,
            @Param("category") ExpenseCategory category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT e.category, COUNT(e), SUM(e.totalAmount) FROM Expense e " +
            "WHERE e.branch.id = :branchId " +
            "AND e.status = 'PAID' " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.totalAmount) DESC")
    List<Object[]> getExpenseStatisticsByCategory(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.branch.id = :branchId " +
            "AND e.status = :status")
    long countByBranchAndStatus(@Param("branchId") Long branchId,
            @Param("status") ExpenseStatus status);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.branch.id = :branchId " +
            "AND e.category = :category " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate")
    long countByCategoryAndDateRange(@Param("branchId") Long branchId,
            @Param("category") ExpenseCategory category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Monthly/Yearly Analysis
    @Query("SELECT YEAR(e.expenseDate), MONTH(e.expenseDate), SUM(e.totalAmount) " +
            "FROM Expense e " +
            "WHERE e.branch.id = :branchId " +
            "AND e.status = 'PAID' " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(e.expenseDate), MONTH(e.expenseDate) " +
            "ORDER BY YEAR(e.expenseDate), MONTH(e.expenseDate)")
    List<Object[]> getMonthlyExpenseTrend(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Budget Related
    List<Expense> findByBudgetId(Long budgetId);

    List<Expense> findByIsBudgetedTrue();

    @Query("SELECT e FROM Expense e WHERE e.branch.id = :branchId " +
            "AND e.isBudgeted = false " +
            "AND e.status = 'PAID'")
    List<Expense> findUnbudgetedExpensesByBranch(@Param("branchId") Long branchId);

    // Recent Expenses
    @Query("SELECT e FROM Expense e WHERE e.branch.id = :branchId " +
            "ORDER BY e.expenseDate DESC, e.createdAt DESC")
    List<Expense> findRecentExpensesByBranch(@Param("branchId") Long branchId, Pageable pageable);

    // Largest Expenses
    @Query("SELECT e FROM Expense e WHERE e.branch.id = :branchId " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate " +
            "ORDER BY e.totalAmount DESC")
    List<Expense> findLargestExpensesByBranchAndDateRange(@Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
