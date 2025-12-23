package com.badminton.enums;

public enum TransferStatus {
    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    IN_TRANSIT("Đang vận chuyển"),
    COMPLETED("Hoàn thành"),
    REJECTED("Từ chối"),
    CANCELLED("Đã hủy");

    private final String vietnameseName;

    TransferStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
