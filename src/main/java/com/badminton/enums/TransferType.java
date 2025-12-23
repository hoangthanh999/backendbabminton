package com.badminton.enums;

public enum TransferType {
    BRANCH_TO_BRANCH("Chi nhánh sang chi nhánh"),
    SUPPLIER_TO_BRANCH("Nhà cung cấp sang chi nhánh"),
    RETURN("Trả hàng"),
    ADJUSTMENT("Điều chỉnh");

    private final String vietnameseName;

    TransferType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
