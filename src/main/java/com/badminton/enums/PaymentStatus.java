package com.badminton.enums;

public enum PaymentStatus {
    PENDING("Chờ thanh toán"),
    PROCESSING("Đang xử lý"),
    COMPLETED("Hoàn thành"),
    FAILED("Thất bại"),
    REFUNDED("Đã hoàn tiền"),
    CANCELLED("Đã hủy"),
    EXPIRED("Hết hạn");

    private final String vietnameseName;

    PaymentStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public boolean isFinished() {
        return this == COMPLETED || this == FAILED ||
                this == REFUNDED || this == CANCELLED || this == EXPIRED;
    }

    public boolean isSuccessful() {
        return this == COMPLETED;
    }
}
