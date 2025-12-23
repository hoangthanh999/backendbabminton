package com.badminton.util;

import com.badminton.entity.payment.Payment;
import com.badminton.entity.payment.PaymentMethod;
import com.badminton.enums.PaymentStatus;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

public class PaymentUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Generate payment reference number
     */
    public static String generateReferenceNumber(String prefix) {
        return String.format("%s%s%06d",
                prefix,
                LocalDateTime.now().format(FORMATTER),
                (int) (Math.random() * 1000000));
    }

    /**
     * Generate transaction ID
     */
    public static String generateTransactionId() {
        return String.format("TXN%d%06d",
                System.currentTimeMillis(),
                (int) (Math.random() * 1000000));
    }

    /**
     * Calculate payment fee
     */
    public static BigDecimal calculateFee(
            BigDecimal amount,
            PaymentMethod method) {
        return method.calculateFee(amount);
    }

    /**
     * Validate payment amount
     */
    public static boolean validateAmount(
            BigDecimal amount,
            PaymentMethod method) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        return method.isAmountValid(amount);
    }

    /**
     * Check if payment is expired
     */
    public static boolean isPaymentExpired(Payment payment) {
        if (payment.getExpiredAt() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(payment.getExpiredAt());
    }

    /**
     * Calculate expiry time
     */
    public static LocalDateTime calculateExpiryTime(int timeoutSeconds) {
        return LocalDateTime.now().plusSeconds(timeoutSeconds);
    }

    /**
     * Generate payment hash/signature
     */
    public static String generateSignature(
            Map<String, String> params,
            String secretKey) {
        try {
            // Sort parameters
            TreeMap<String, String> sortedParams = new TreeMap<>(params);

            // Build query string
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                queryString.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }

            // Add secret key
            queryString.append("&key=").append(secretKey);

            // Generate SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(queryString.toString().getBytes("UTF-8"));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString().toUpperCase();

        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    /**
     * Verify payment signature
     */
    public static boolean verifySignature(
            Map<String, String> params,
            String signature,
            String secretKey) {
        String calculatedSignature = generateSignature(params, secretKey);
        return calculatedSignature.equals(signature);
    }

    /**
     * Format amount for display
     */
    public static String formatAmount(BigDecimal amount) {
        return String.format("%,.0f đ", amount);
    }

    /**
     * Parse amount from string
     */
    public static BigDecimal parseAmount(String amountStr) {
        try {
            String cleaned = amountStr.replaceAll("[^0-9.]", "");
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid amount format: " + amountStr);
        }
    }

    /**
     * Calculate refund amount based on policy
     */
    public static BigDecimal calculateRefundAmount(
            Payment payment,
            LocalDateTime refundTime) {
        // Simple policy: full refund if within 24 hours
        LocalDateTime paymentTime = payment.getPaidAt();
        if (paymentTime == null) {
            return BigDecimal.ZERO;
        }

        long hoursElapsed = java.time.Duration
                .between(paymentTime, refundTime)
                .toHours();

        if (hoursElapsed <= 24) {
            return payment.getAmount();
        } else if (hoursElapsed <= 72) {
            // 80% refund
            return payment.getAmount()
                    .multiply(new BigDecimal("0.80"));
        } else {
            // 50% refund
            return payment.getAmount()
                    .multiply(new BigDecimal("0.50"));
        }
    }

    /**
     * Check if payment can be refunded
     */
    public static boolean canBeRefunded(Payment payment) {
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return false;
        }

        if (payment.isRefunded()) {
            return false;
        }

        // Check if within refund period (e.g., 30 days)
        if (payment.getPaidAt() == null) {
            return false;
        }

        LocalDateTime refundDeadline = payment.getPaidAt().plusDays(30);
        return LocalDateTime.now().isBefore(refundDeadline);
    }

    /**
     * Get payment status color
     */
    public static String getStatusColor(PaymentStatus status) {
        return switch (status) {
            case COMPLETED -> "#28a745"; // Green
            case PENDING, PROCESSING -> "#ffc107"; // Yellow
            case FAILED, CANCELLED -> "#dc3545"; // Red
            case REFUNDED -> "#17a2b8"; // Blue
            case EXPIRED -> "#6c757d"; // Gray
        };
    }
}
