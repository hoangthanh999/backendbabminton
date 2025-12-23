package com.badminton.enums;

public enum SharePlatform {
    FACEBOOK("Facebook"),
    TWITTER("Twitter"),
    LINKEDIN("LinkedIn"),
    ZALO("Zalo"),
    EMAIL("Email"),
    COPY_LINK("Copy Link"),
    WHATSAPP("WhatsApp"),
    TELEGRAM("Telegram"),
    OTHER("Khác");

    private final String vietnameseName;

    SharePlatform(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
