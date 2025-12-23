package com.badminton.enums;

public enum ReturnReason {
    DEFECTIVE("Lỗi sản phẩm"),
    WRONG_ITEM("Sai sản phẩm"),
    NOT_AS_DESCRIBED("Không đúng mô tả"),
    CHANGED_MIND("Đổi ý"),
    DAMAGED("Hư hỏng"),
    OTHER("Khác");

    private final String vietnameseName;

    ReturnReason(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
