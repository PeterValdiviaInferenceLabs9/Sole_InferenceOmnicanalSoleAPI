package com.inference.whatsappintegration.util.enums;

public enum EnumBotStatus {
    OPEN,
    CLOSE,
    TRANSFER;

    public static EnumBotStatus fromString(String value) {
        for (EnumBotStatus status : EnumBotStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid BotStatus value: " + value);
    }
}

