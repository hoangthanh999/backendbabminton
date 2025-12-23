package com.badminton.enums;

public enum RefundStatus {
    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    PROCESSING("Đang xử lý"),
    COMPLETED("Hoàn thành"),
    REJECTED("Từ chối"),
    FAILED("Thất bại");

    private final String vietnameseName;

    RefundStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
