package com.badminton.enums;

public enum OrderType {
    RETAIL("Bán lẻ"),
    WHOLESALE("Bán sỉ"),
    ONLINE("Trực tuyến"),
    WALK_IN("Tại quầy");

    private final String vietnameseName;

    OrderType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
