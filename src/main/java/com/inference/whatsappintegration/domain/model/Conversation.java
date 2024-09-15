package com.inference.whatsappintegration.domain.model;

import com.inference.whatsappintegration.application.dto.whatsappinrequest.Location;
import com.inference.whatsappintegration.util.enums.EnumConversationStatus;
import lombok.*;

@Builder
@Getter
@Setter
public class Conversation {

    private String conversationId;

    private String sessionId;

    private String clientId;

    private String imSubject;

    private String clientName;

    private String messageText;

    private String messageAttachment;

    private Location location;

    private String messageResponse;

    private String agentSurvey;

    private String messageTextType;

    private String subjectId;

    private String cascadeId;

    private EnumConversationStatus status;

    private TransferAgentInformation transferAgentInformation;

}

