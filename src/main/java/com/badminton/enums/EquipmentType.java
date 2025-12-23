package com.badminton.enums;

public enum EquipmentType {
    RACKET("Vợt cầu lông"),
    SHUTTLECOCK("Quả cầu"),
    NET("Lưới"),
    SHOES("Giày"),
    BAG("Túi đựng vợt"),
    GRIP("Quấn cán vợt"),
    STRING("Dây vợt"),
    COURT_EQUIPMENT("Thiết bị sân"),
    TRAINING_EQUIPMENT("Thiết bị tập luyện"),
    LIGHTING("Đèn chiếu sáng"),
    SCOREBOARD("Bảng điểm"),
    FURNITURE("Nội thất"),
    CLEANING("Thiết bị vệ sinh"),
    SAFETY("Thiết bị an toàn"),
    OTHER("Khác");

    private final String vietnameseName;

    EquipmentType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
