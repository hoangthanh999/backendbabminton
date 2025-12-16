package com.badminton.enums;

public enum MaintenanceType {
    ROUTINE("Bảo trì định kỳ"),
    REPAIR("Sửa chữa"),
    UPGRADE("Nâng cấp"),
    EMERGENCY("Khẩn cấp");

    private final String vietnameseName;

    MaintenanceType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
