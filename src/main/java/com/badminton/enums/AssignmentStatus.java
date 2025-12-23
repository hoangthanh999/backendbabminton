package com.badminton.enums;

public enum AssignmentStatus {
    ACTIVE("Đang sử dụng"),
    RETURNED("Đã trả"),
    OVERDUE("Quá hạn"),
    LOST("Mất"),
    DAMAGED("Hư hỏng");

    private final String vietnameseName;

    AssignmentStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
