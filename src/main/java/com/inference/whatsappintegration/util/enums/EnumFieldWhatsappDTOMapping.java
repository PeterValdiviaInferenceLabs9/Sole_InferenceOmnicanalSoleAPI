package com.inference.whatsappintegration.util.enums;

public enum EnumFieldWhatsappDTOMapping {
    ID("id", "Please add value for id to your body request"),
    SUBSCRIBER("subscriber", "Please add value for subscriber to your body request"),
    SUBJECT("subject", "Please add subject to your body request"),
    MESSAGE_CONTENT("messageContent", "Please add the message content to your body request");

    private final String fieldName;
    private final String errorMessage;

    EnumFieldWhatsappDTOMapping(String fieldName, String errorMessage) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
