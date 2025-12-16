package com.badminton.enums;

// CourtStatus.java
public enum CourtStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động"),
    MAINTENANCE("Bảo trì");

    private final String vietnameseName;

    CourtStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
