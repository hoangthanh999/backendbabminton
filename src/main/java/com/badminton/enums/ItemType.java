package com.badminton.enums;

public enum ItemType {
    PRODUCT("Sản phẩm"),
    SERVICE("Dịch vụ");

    private final String vietnameseName;

    ItemType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
