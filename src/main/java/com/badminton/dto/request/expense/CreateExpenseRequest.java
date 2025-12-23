package com.badminton.dto.request.expense;

import com.badminton.enums.ExpenseCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Create expense request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpenseRequest {

    @NotNull(message = "Chi nhánh không được để trống")
    private Long branchId;

    @NotNull(message = "Danh mục không được để trống")
    private ExpenseCategory category;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 5, max = 200, message = "Tiêu đề phải từ 5-200 ký tự")
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 2000, message = "Mô tả không quá 2000 ký tự")
    private String description;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải > 0")
    private BigDecimal amount;

    @NotNull(message = "Ngày chi không được để trống")
    private LocalDate expenseDate;

    private String vendor;

    private String invoiceNumber;

    private String paymentMethod;

    private List<String> attachments;

    private String notes;

    private Boolean requiresApproval;
}
