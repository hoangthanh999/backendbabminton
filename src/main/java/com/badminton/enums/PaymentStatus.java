package com.badminton.enums;

public enum PaymentStatus {
    UNPAID("Chưa thanh toán"),
    PARTIAL("Thanh toán một phần"),
    DEPOSIT("Đã đặt cọc"),
    PAID("Đã thanh toán");

    private final String vietnameseName;

    PaymentStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public boolean isPaid() {
        return this == PAID;
    }

    public boolean needsPayment() {
        return this == UNPAID || this == PARTIAL || this == DEPOSIT;
    }
}