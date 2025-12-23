package com.badminton.enums;

// PostStatus.java
public enum PostStatus {
    DRAFT("Bản nháp"),
    PENDING("Chờ duyệt"),
    SCHEDULED("Đã lên lịch"),
    PUBLISHED("Đã xuất bản"),
    ARCHIVED("Lưu trữ");

    private final String vietnameseName;

    PostStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
