package com.badminton.enums;

public enum TournamentType {
    SINGLES("Đơn"),
    DOUBLES("Đôi"),
    MIXED_DOUBLES("Đôi nam nữ"),
    TEAM("Đồng đội"),
    MIXED("Hỗn hợp");

    private final String vietnameseName;

    TournamentType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}