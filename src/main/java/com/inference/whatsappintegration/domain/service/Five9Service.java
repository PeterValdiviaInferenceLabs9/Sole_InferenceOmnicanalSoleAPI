package com.inference.whatsappintegration.domain.service;

import com.inference.whatsappintegration.application.dto.conversationsdto.five9acknowledgemessagerequest.Five9AcknowledgeMessageRequest;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9conversationsendmessagerequest.SendConversationMessageRequest;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9createconversationrequest.Five9CreateConversationRequest;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9createconversationresponse.Five9CreateConversationResponse;
import com.inference.whatsappintegration.application.dto.five9tokenrequest.Five9TokenRequest;
import com.inference.whatsappintegration.application.dto.five9tokenresponse.Five9TokenResponse;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Five9Session;

public interface Five9Service {

    Five9TokenResponse getToken(Five9TokenRequest five9TokenRequest);

    Five9CreateConversationResponse createConversation(Five9CreateConversationRequest five9CreateConversationRequest, Five9Session five9Session);

    int sendFive9ConversationMessage(Five9Session five9Session, SendConversationMessageRequest sendConversationMessageRequest);

    void sendAcknowledgeMessageToConversation(Five9Session five9Session,
                                              Five9AcknowledgeMessageRequest five9AcknowledgeMessageRequest);

}
