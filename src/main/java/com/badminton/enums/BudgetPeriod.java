package com.badminton.enums;

public enum BudgetPeriod {
    DAILY("Hàng ngày"),
    WEEKLY("Hàng tuần"),
    MONTHLY("Hàng tháng"),
    QUARTERLY("Hàng quý"),
    YEARLY("Hàng năm");

    private final String vietnameseName;

    BudgetPeriod(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
