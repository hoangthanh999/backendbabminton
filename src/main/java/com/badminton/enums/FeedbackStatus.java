package com.badminton.enums;

public enum FeedbackStatus {
    PENDING("Chờ xử lý"),
    APPROVED("Đã duyệt"),
    REJECTED("Từ chối"),
    RESPONDED("Đã phản hồi"),
    RESOLVED("Đã giải quyết"),
    CLOSED("Đã đóng");

    private final String vietnameseName;

    FeedbackStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
