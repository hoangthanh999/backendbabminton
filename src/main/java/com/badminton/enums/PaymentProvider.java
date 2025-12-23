package com.badminton.enums;

public enum PaymentProvider {
    CASH("Tiền mặt"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    VNPAY("VNPay"),
    MOMO("MoMo"),
    ZALOPAY("ZaloPay"),
    CREDIT_CARD("Thẻ tín dụng"),
    DEBIT_CARD("Thẻ ghi nợ"),
    QR_CODE("Mã QR"),
    OTHER("Khác");

    private final String vietnameseName;

    PaymentProvider(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public boolean isOnline() {
        return this != CASH && this != BANK_TRANSFER;
    }

    public boolean requiresGateway() {
        return this == VNPAY || this == MOMO || this == ZALOPAY;
    }
}