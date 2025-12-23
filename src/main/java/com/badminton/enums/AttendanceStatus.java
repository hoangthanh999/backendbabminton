package com.badminton.enums;

public enum AttendanceStatus {
    PRESENT("Có mặt"),
    ABSENT("Vắng mặt"),
    ON_LEAVE("Nghỉ phép"),
    LATE("Đi muộn"),
    HALF_DAY("Nửa ngày"),
    REMOTE("Làm từ xa");

    private final String vietnameseName;

    AttendanceStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
