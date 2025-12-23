package com.badminton.enums;

public enum PaymentType {
    FULL("Thanh toán đầy đủ"),
    DEPOSIT("Đặt cọc"),
    PARTIAL("Thanh toán một phần"),
    INSTALLMENT("Trả góp"),
    BALANCE("Thanh toán số dư");

    private final String vietnameseName;

    PaymentType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
