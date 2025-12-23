package com.badminton.enums;

public enum EquipmentCondition {
    EXCELLENT("Xuất sắc"),
    GOOD("Tốt"),
    FAIR("Khá"),
    POOR("Kém"),
    BROKEN("Hỏng");

    private final String vietnameseName;

    EquipmentCondition(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}