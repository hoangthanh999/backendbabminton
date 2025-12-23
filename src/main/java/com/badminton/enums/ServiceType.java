package com.badminton.enums;

public enum ServiceType {
    STRINGING("Căng vợt"),
    MAINTENANCE("Bảo dưỡng"),
    TRAINING("Huấn luyện"),
    RENTAL("Cho thuê"),
    OTHER("Khác");

    private final String vietnameseName;

    ServiceType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
