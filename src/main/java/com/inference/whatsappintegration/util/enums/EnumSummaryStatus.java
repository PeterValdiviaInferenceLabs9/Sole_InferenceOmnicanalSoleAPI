package com.inference.whatsappintegration.util.enums;

public enum EnumSummaryStatus {

    IN_PROGRESS("IN_PROGRESS", "IN_PROGRESS"),
    SELF_MANAGEMENT("SELF_MANAGEMENT", "SELF_MANAGEMENT"),
    TRANSFER_AGENT("TRANSFER_AGENT", "TRANSFER_AGENT"),
    AGENT_TERMINATE("AGENT_TERMINATE", "AGENT_TERMINATE");

    private final String fieldName;
    private final String status;

    EnumSummaryStatus(String fieldName, String status) {
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
