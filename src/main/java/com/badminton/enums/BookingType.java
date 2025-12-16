package com.badminton.enums;

public enum BookingType {
    SINGLE("Đặt đơn"),
    RECURRING("Đặt định kỳ"),
    TOURNAMENT("Giải đấu"),
    TRAINING("Tập luyện");

    private final String vietnameseName;

    BookingType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
