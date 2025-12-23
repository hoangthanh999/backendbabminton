package com.badminton.enums;

public enum PointTransactionType {
    EARNED_BOOKING("Tích điểm từ đặt sân"),
    EARNED_ORDER("Tích điểm từ mua hàng"),
    EARNED_REFERRAL("Tích điểm giới thiệu"),
    EARNED_BONUS("Điểm thưởng"),
    EARNED_BIRTHDAY("Điểm sinh nhật"),
    EARNED_PROMOTION("Điểm khuyến mãi"),
    REDEEMED_DISCOUNT("Đổi điểm giảm giá"),
    REDEEMED_GIFT("Đổi điểm quà tặng"),
    REDEEMED_VOUCHER("Đổi điểm voucher"),
    EXPIRED("Hết hạn"),
    ADJUSTED("Điều chỉnh"),
    CANCELLED("Hủy");

    private final String vietnameseName;

    PointTransactionType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public boolean isEarning() {
        return this.name().startsWith("EARNED_");
    }

    public boolean isRedemption() {
        return this.name().startsWith("REDEEMED_");
    }
}
