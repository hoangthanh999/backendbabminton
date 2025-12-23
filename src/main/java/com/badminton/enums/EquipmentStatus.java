package com.badminton.enums;

public enum EquipmentStatus {
    AVAILABLE("Có sẵn"),
    IN_USE("Đang sử dụng"),
    MAINTENANCE("Bảo trì"),
    RETIRED("Ngừng sử dụng"),
    LOST("Mất"),
    DAMAGED("Hư hỏng");

    private final String vietnameseName;

    EquipmentStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
