package com.badminton.enums;

public enum ImageType {
    MAIN("Ảnh chính"),
    GALLERY("Thư viện"),
    FACILITY("Tiện nghi"),
    PANORAMA("Toàn cảnh");

    private final String vietnameseName;

    ImageType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
