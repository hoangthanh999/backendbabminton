package com.badminton.enums;

public enum PostType {
    ARTICLE("Bài viết"),
    NEWS("Tin tức"),
    TUTORIAL("Hướng dẫn"),
    VIDEO("Video"),
    GALLERY("Thư viện ảnh"),
    ANNOUNCEMENT("Thông báo"),
    EVENT("Sự kiện"),
    PROMOTION("Khuyến mãi");

    private final String vietnameseName;

    PostType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
