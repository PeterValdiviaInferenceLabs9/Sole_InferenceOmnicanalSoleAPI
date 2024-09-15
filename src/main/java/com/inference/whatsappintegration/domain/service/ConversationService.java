package com.inference.whatsappintegration.domain.service;

import com.inference.whatsappintegration.domain.model.Conversation;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;

public interface ConversationService {
    Conversation sendConversationToInferenceBot(Conversation conversation);

    Conversation sendConversationToInferenceSurveyBot(Conversation conversation);

    Conversation processDirectSubjectTransfer(Conversation conversation);

    void sendConversationWhatsappResponse(Conversation conversation) throws InterruptedException;

    void sendConversationToFive9Agent(Conversation conversation, Sessions incomingSession);
}
