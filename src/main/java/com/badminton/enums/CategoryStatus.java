package com.badminton.enums;

public enum CategoryStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động");

    private final String vietnameseName;

    CategoryStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
