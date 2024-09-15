package com.inference.whatsappintegration.util.enums;

public enum EnumAcknowledgeType {
    DELIVERED("DELIVERED", "DELIVERED"),
    READ("READ", "READ"),
    FAILED("FAILED", "FAILED");

    private final String fieldName;
    private final String status;

    EnumAcknowledgeType(String fieldName, String status) {
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
