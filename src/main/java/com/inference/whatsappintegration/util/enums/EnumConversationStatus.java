package com.inference.whatsappintegration.util.enums;

public enum EnumConversationStatus {
    IN_PROGRESS("IN_PROGRESS", "IN_PROGRESS"),
    TERMINATED("TERMINATED", "TERMINATED"),
    TRANSFER_AGENT("TRANSFER_AGENT", "TRANSFER_AGENT");

    private final String fieldName;
    private final String status;

    EnumConversationStatus(String fieldName, String status) {
        this.fieldName = fieldName;
        this.status = status;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getStatus() {
        return status;
    }
}
