package com.badminton.enums;

public enum ReturnStatus {
    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    REJECTED("Từ chối"),
    PROCESSING("Đang xử lý"),
    COMPLETED("Hoàn thành");

    private final String vietnameseName;

    ReturnStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
