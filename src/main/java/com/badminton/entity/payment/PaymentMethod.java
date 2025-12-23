package com.badminton.entity.payment;

import com.badminton.entity.base.BaseEntity;
import com.badminton.enums.PaymentProvider;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payment_methods", uniqueConstraints = @UniqueConstraint(name = "uk_name_provider", columnNames = {
        "name", "provider" }), indexes = {
                @Index(name = "idx_provider", columnList = "provider"),
                @Index(name = "idx_active", columnList = "is_active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name; // Tiền mặt, Chuyển khoản, VNPay, MoMo

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 50)
    private PaymentProvider provider;

    @Column(name = "code", unique = true, length = 50)
    private String code; // CASH, BANK_TRANSFER, VNPAY, MOMO, ZALOPAY

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_online")
    @Builder.Default
    private Boolean isOnline = false;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    // Configuration
    @Column(name = "min_amount", precision = 10, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "max_amount", precision = 10, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "fee_type", length = 20)
    private String feeType; // FIXED, PERCENTAGE

    @Column(name = "fee_value", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal feeValue = BigDecimal.ZERO;

    @Column(name = "fee_cap", precision = 10, scale = 2)
    private BigDecimal feeCap; // Maximum fee

    // Gateway Configuration
    @Column(name = "api_endpoint")
    private String apiEndpoint;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "secret_key", columnDefinition = "TEXT")
    private String secretKey;

    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "return_url")
    private String returnUrl;

    @Column(name = "callback_url")
    private String callbackUrl;

    @Column(name = "timeout_seconds")
    @Builder.Default
    private Integer timeoutSeconds = 900; // 15 minutes

    // Display Info
    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "color_code", length = 7)
    private String colorCode; // Hex color

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions; // Payment instructions

    // Bank Info (for bank transfer)
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_account_name")
    private String bankAccountName;

    @Column(name = "bank_branch")
    private String bankBranch;

    // Statistics
    @Column(name = "total_transactions")
    @Builder.Default
    private Long totalTransactions = 0L;

    @Column(name = "successful_transactions")
    @Builder.Default
    private Long successfulTransactions = 0L;

    @Column(name = "total_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Metadata
    @Column(name = "settings", columnDefinition = "JSON")
    private String settings;

    // Relationships
    @OneToMany(mappedBy = "method")
    @Builder.Default
    private Set<Payment> payments = new HashSet<>();

    // Helper Methods

    /**
     * Calculate fee for amount
     */
    public BigDecimal calculateFee(BigDecimal amount) {
        if (feeValue == null || feeValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal fee;

        if ("PERCENTAGE".equals(feeType)) {
            fee = amount.multiply(feeValue).divide(
                    new BigDecimal("100"),
                    2,
                    java.math.RoundingMode.HALF_UP);

            // Apply fee cap if exists
            if (feeCap != null && fee.compareTo(feeCap) > 0) {
                fee = feeCap;
            }
        } else {
            // FIXED fee
            fee = feeValue;
        }

        return fee;
    }

    /**
     * Check if amount is valid
     */
    public boolean isAmountValid(BigDecimal amount) {
        if (minAmount != null && amount.compareTo(minAmount) < 0) {
            return false;
        }

        if (maxAmount != null && amount.compareTo(maxAmount) > 0) {
            return false;
        }

        return true;
    }

    /**
     * Increment statistics
     */
    public void incrementStatistics(BigDecimal amount, boolean successful) {
        this.totalTransactions++;

        if (successful) {
            this.successfulTransactions++;
            this.totalAmount = this.totalAmount.add(amount);
        }
    }

    /**
     * Get success rate
     */
    public BigDecimal getSuccessRate() {
        if (totalTransactions == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(successfulTransactions)
                .divide(BigDecimal.valueOf(totalTransactions), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Check if is cash payment
     */
    public boolean isCash() {
        return provider == PaymentProvider.CASH;
    }

    /**
     * Check if is bank transfer
     */
    public boolean isBankTransfer() {
        return provider == PaymentProvider.BANK_TRANSFER;
    }

    /**
     * Check if is e-wallet
     */
    public boolean isEWallet() {
        return provider == PaymentProvider.MOMO ||
                provider == PaymentProvider.ZALOPAY;
    }

    /**
     * Check if requires gateway
     */
    public boolean requiresGateway() {
        return isOnline && provider != PaymentProvider.CASH
                && provider != PaymentProvider.BANK_TRANSFER;
    }
}
