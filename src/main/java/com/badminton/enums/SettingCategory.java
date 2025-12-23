package com.badminton.enums;

public enum SettingCategory {
    GENERAL("Chung"),
    BOOKING("Đặt sân"),
    PAYMENT("Thanh toán"),
    NOTIFICATION("Thông báo"),
    EMAIL("Email"),
    SMS("SMS"),
    LOYALTY("Khách hàng thân thiết"),
    SECURITY("Bảo mật"),
    APPEARANCE("Giao diện"),
    INTEGRATION("Tích hợp"),
    MAINTENANCE("Bảo trì"),
    BUSINESS("Kinh doanh");

    private final String vietnameseName;

    SettingCategory(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}