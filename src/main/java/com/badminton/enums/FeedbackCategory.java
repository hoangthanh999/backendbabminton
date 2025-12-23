package com.badminton.enums;

public enum FeedbackCategory {
    COMPLAINT("Khiếu nại"),
    SUGGESTION("Đề xuất"),
    COMPLIMENT("Khen ngợi"),
    QUESTION("Câu hỏi"),
    BUG_REPORT("Báo lỗi"),
    FEATURE_REQUEST("Yêu cầu tính năng");

    private final String vietnameseName;

    FeedbackCategory(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
