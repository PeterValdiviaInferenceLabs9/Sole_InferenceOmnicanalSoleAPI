package com.inference.whatsappintegration.domain.service;

import com.inference.whatsappintegration.application.dto.conversationseventsdto.conversationmessageeventrequest.ConversationMessageEventRequest;
import com.inference.whatsappintegration.domain.model.Conversation;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;

public interface DatabaseOperationsService {

    void insertClientInteraction(Conversation conversation, Sessions incomingSession);

    void insertBotMetricsInteraction(Conversation conversation);

    void insertFive9Interaction(ConversationMessageEventRequest conversationMessageEventRequest,
                                Sessions incomingSession);

    void processClientSummaryInteraction(Conversation conversation);

    void processFive9CountSummaryInteraction(Conversation conversation);

    void processFive9CountSummaryTerminateInteraction(String conversationId);

}
