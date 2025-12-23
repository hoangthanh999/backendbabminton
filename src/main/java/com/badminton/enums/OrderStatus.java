package com.badminton.enums;

public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    PROCESSING("Đang xử lý"),
    SHIPPED("Đã giao vận"),
    DELIVERED("Đã giao hàng"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    private final String vietnameseName;

    OrderStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public boolean isActive() {
        return this != CANCELLED && this != COMPLETED;
    }

    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED;
    }
}
