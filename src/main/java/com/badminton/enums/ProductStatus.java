package com.badminton.enums;

public enum ProductStatus {
    ACTIVE("Đang bán"),
    INACTIVE("Ngừng bán"),
    OUT_OF_STOCK("Hết hàng"),
    DISCONTINUED("Ngừng kinh doanh"),
    DRAFT("Bản nháp");

    private final String vietnameseName;

    ProductStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}