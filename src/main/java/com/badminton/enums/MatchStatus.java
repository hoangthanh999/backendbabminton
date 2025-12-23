package com.badminton.enums;

public enum MatchStatus {
    SCHEDULED("Đã lên lịch"),
    IN_PROGRESS("Đang diễn ra"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy"),
    POSTPONED("Hoãn lại"),
    WALKOVER("Walkover");

    private final String vietnameseName;

    MatchStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}