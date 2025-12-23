package com.badminton.enums;

public enum FeedbackType {
    BOOKING("Đặt sân"),
    ORDER("Đơn hàng"),
    SERVICE("Dịch vụ"),
    FACILITY("Cơ sở vật chất"),
    STAFF("Nhân viên"),
    GENERAL("Chung");

    private final String vietnameseName;

    FeedbackType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
