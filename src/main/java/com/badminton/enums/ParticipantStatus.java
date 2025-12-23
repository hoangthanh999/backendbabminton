package com.badminton.enums;

public enum ParticipantStatus {
    PENDING("Chờ duyệt"),
    CONFIRMED("Đã xác nhận"),
    REJECTED("Từ chối"),
    WITHDRAWN("Rút lui"),
    DISQUALIFIED("Loại");

    private final String vietnameseName;

    ParticipantStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
