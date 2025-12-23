package com.badminton.enums;

public enum TransactionType {
    CHARGE("Thanh toán"),
    REFUND("Hoàn tiền"),
    CHARGEBACK("Hoàn trả"),
    ADJUSTMENT("Điều chỉnh"),
    FEE("Phí");

    private final String vietnameseName;

    TransactionType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
