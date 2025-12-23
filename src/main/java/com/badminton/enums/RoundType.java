package com.badminton.enums;

public enum RoundType {
    PRELIMINARY("Vòng sơ loại"),
    ROUND_OF_64("Vòng 64"),
    ROUND_OF_32("Vòng 32"),
    ROUND_OF_16("Vòng 16"),
    QUARTER_FINAL("Tứ kết"),
    SEMI_FINAL("Bán kết"),
    THIRD_PLACE("Tranh hạng 3"),
    FINAL("Chung kết");

    private final String vietnameseName;

    RoundType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
