package com.badminton.dto.response.expense;

import com.badminton.enums.ExpenseCategory;
import com.badminton.enums.ExpenseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Expense response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseResponse {

    private Long id;
    private String expenseNumber;

    private BranchInfo branch;
    private UserInfo createdBy;

    private ExpenseCategory category;
    private ExpenseStatus status;

    private String title;
    private String description;

    private BigDecimal amount;
    private LocalDate expenseDate;

    private String vendor;
    private String invoiceNumber;
    private String paymentMethod;

    private List<String> attachments;
    private String notes;

    private Boolean requiresApproval;
    private UserInfo approvedBy;
    private LocalDateTime approvedAt;
    private String approvalNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchInfo {
        private Long id;
        private String name;
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
    }
}
