package com.badminton.enums;

public enum ExpenseStatus {
    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    REJECTED("Từ chối"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy");

    private final String vietnameseName;

    ExpenseStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
