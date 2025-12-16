package com.badminton.enums;

public enum DayOfWeek {
    MONDAY(1, "Thứ Hai"),
    TUESDAY(2, "Thứ Ba"),
    WEDNESDAY(3, "Thứ Tư"),
    THURSDAY(4, "Thứ Năm"),
    FRIDAY(5, "Thứ Sáu"),
    SATURDAY(6, "Thứ Bảy"),
    SUNDAY(7, "Chủ Nhật");

    private final int value;
    private final String vietnameseName;

    DayOfWeek(int value, String vietnameseName) {
        this.value = value;
        this.vietnameseName = vietnameseName;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public static DayOfWeek fromValue(int value) {
        for (DayOfWeek day : values()) {
            if (day.value == value) {
                return day;
            }
        }
        throw new IllegalArgumentException("Invalid day of week: " + value);
    }

    public static DayOfWeek fromJavaTime(java.time.DayOfWeek javaDayOfWeek) {
        return fromValue(javaDayOfWeek.getValue());
    }
}