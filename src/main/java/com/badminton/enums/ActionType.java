package com.badminton.enums;

public enum ActionType {
    CREATE("Tạo mới"),
    READ("Xem"),
    UPDATE("Cập nhật"),
    DELETE("Xóa"),
    LOGIN("Đăng nhập"),
    LOGOUT("Đăng xuất"),
    EXPORT("Xuất dữ liệu"),
    IMPORT("Nhập dữ liệu"),
    APPROVE("Phê duyệt"),
    REJECT("Từ chối"),
    CANCEL("Hủy"),
    RESTORE("Khôi phục");

    private final String vietnameseName;

    ActionType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
