package com.badminton.enums;

public enum MaintenanceStatus {
    SCHEDULED("Đã lên lịch"),
    IN_PROGRESS("Đang thực hiện"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    private final String vietnameseName;

    MaintenanceStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}