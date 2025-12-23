package com.badminton.enums;

public enum SettingType {
    STRING("Chuỗi"),
    INTEGER("Số nguyên"),
    DECIMAL("Số thập phân"),
    BOOLEAN("Đúng/Sai"),
    JSON("JSON"),
    TEXT("Văn bản"),
    DATE("Ngày"),
    TIME("Giờ"),
    DATETIME("Ngày giờ"),
    URL("Đường dẫn"),
    EMAIL("Email"),
    COLOR("Màu sắc");

    private final String vietnameseName;

    SettingType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
