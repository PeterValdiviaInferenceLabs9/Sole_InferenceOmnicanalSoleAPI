package com.inference.whatsappintegration.application.dto.conversationseventsdto.conversationmessageeventrequest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ConversationMessageEventRequest {

    private String correlationId;

    private String displayName;

    private Long eventSerialNumber;

    private String from;

    private String messageId;

    private String text;

    private String timestamp;

    private Long userId;

}
