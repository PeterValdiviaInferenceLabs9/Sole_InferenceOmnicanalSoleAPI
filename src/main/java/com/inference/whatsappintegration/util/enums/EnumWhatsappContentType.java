package com.inference.whatsappintegration.util.enums;

public enum EnumWhatsappContentType {

    TEXT("[TEXT]", "TEXT"),
    IMAGE("[IMAGE]", "IMAGE"),
    DOCUMENT("[DOCUMENT]", "DOCUMENT"),
    VIDEO("[VIDEO]", "VIDEO"),
    AUDIO("[AUDIO]", "AUDIO");

    private final String fieldName;
    private final String contentType;

    EnumWhatsappContentType(String fieldName, String contentType) {
        this.fieldName = fieldName;
        this.contentType = contentType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getContentType() {
        return contentType;
    }

    public static EnumWhatsappContentType fromFieldName(String fieldName) {
        for (EnumWhatsappContentType type : EnumWhatsappContentType.values()) {
            if (type.getFieldName().equals(fieldName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant found for field name: " + fieldName);
    }

}
