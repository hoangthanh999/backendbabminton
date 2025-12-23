package com.badminton.enums;

public enum ExpenseCategory {
    SALARY("Lương nhân viên"),
    RENT("Thuê mặt bằng"),
    UTILITIES("Điện nước"),
    MAINTENANCE("Bảo trì"),
    EQUIPMENT("Thiết bị"),
    SUPPLIES("Vật tư"),
    MARKETING("Marketing"),
    INSURANCE("Bảo hiểm"),
    TAX("Thuế"),
    TRAINING("Đào tạo"),
    TRAVEL("Đi lại"),
    COMMUNICATION("Thông tin liên lạc"),
    OFFICE("Văn phòng phẩm"),
    CLEANING("Vệ sinh"),
    SECURITY("An ninh"),
    LEGAL("Pháp lý"),
    CONSULTING("Tư vấn"),
    SOFTWARE("Phần mềm"),
    INVENTORY("Hàng tồn kho"),
    OTHER("Khác");

    private final String vietnameseName;

    ExpenseCategory(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
