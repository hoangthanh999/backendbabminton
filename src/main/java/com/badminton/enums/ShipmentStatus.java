package com.badminton.enums;

public enum ShipmentStatus {
    PENDING("Chờ giao"),
    SHIPPED("Đã giao vận"),
    IN_TRANSIT("Đang vận chuyển"),
    OUT_FOR_DELIVERY("Đang giao hàng"),
    DELIVERED("Đã giao"),
    RETURNED("Đã trả lại"),
    CANCELLED("Đã hủy");

    private final String vietnameseName;

    ShipmentStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
