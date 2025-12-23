package com.badminton.enums;

public enum TournamentFormat {
    SINGLE_ELIMINATION("Loại trực tiếp"),
    DOUBLE_ELIMINATION("Loại kép"),
    ROUND_ROBIN("Vòng tròn"),
    SWISS("Swiss"),
    LADDER("Bậc thang"),
    KNOCKOUT("Knock-out");

    private final String vietnameseName;

    TournamentFormat(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
