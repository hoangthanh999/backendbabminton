package com.badminton.enums;

public enum TournamentStatus {
    DRAFT("Bản nháp"),
    OPEN("Đang mở đăng ký"),
    REGISTRATION_CLOSED("Đóng đăng ký"),
    IN_PROGRESS("Đang diễn ra"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy"),
    POSTPONED("Hoãn lại");

    private final String vietnameseName;

    TournamentStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
