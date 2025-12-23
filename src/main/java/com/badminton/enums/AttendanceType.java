package com.badminton.enums;

public enum AttendanceType {
    WORK("Làm việc"),
    TRAINING("Đào tạo"),
    MEETING("Họp"),
    OVERTIME("Tăng ca"),
    BUSINESS_TRIP("Công tác");

    private final String vietnameseName;

    AttendanceType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
