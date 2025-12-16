package com.badminton.enums;

public enum BookingStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy"),
    NO_SHOW("Không đến");

    private final String vietnameseName;

    BookingStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public boolean isActive() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }
}