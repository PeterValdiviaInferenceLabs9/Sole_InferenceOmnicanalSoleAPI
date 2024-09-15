package com.inference.whatsappintegration.application.mapper;

import com.inference.whatsappintegration.application.dto.conversationsdto.five9acknowledgemessagerequest.AcknowledgeMessage;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9acknowledgemessagerequest.Five9AcknowledgeMessageRequest;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9conversationsendmessagerequest.SendConversationMessageRequest;
import com.inference.whatsappintegration.application.dto.conversationseventsdto.conversationmessageeventrequest.ConversationMessageEventRequest;
import com.inference.whatsappintegration.application.dto.inferenceoutrequest.InferenceOutRequest;
import com.inference.whatsappintegration.application.dto.inferenceoutrequest.MessageJson;
import com.inference.whatsappintegration.application.dto.inferenceoutresponse.InferenceOutResponse;
import com.inference.whatsappintegration.application.dto.whatsappinrequest.Attachment;
import com.inference.whatsappintegration.application.dto.whatsappinrequest.WhatsappIncomingMessageRequestDTO;
import com.inference.whatsappintegration.domain.model.Conversation;
import com.inference.whatsappintegration.domain.model.TransferAgentInformation;
import com.inference.whatsappintegration.infrastructure.config.WhatsappSubjectProperties;
import com.inference.whatsappintegration.infrastructure.persistence.entity.ConversationCountSummary;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;
import com.inference.whatsappintegration.infrastructure.persistence.entity.WhatsappDailyConversationHistory;
import com.inference.whatsappintegration.util.Constants;
import com.inference.whatsappintegration.util.Utils;
import com.inference.whatsappintegration.util.enums.EnumAcknowledgeType;
import com.inference.whatsappintegration.util.enums.EnumConversationStatus;
import com.inference.whatsappintegration.util.enums.EnumSummaryStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ConversationMapper {

    @Value("${property.inference.bot.api.key}")
    private String apiKeyInferenceBot;

    @Value("${property.inference.bot.main.task.key}")
    private String taskKeyMainAttention;

    @Value("${property.inference.bot.survey.task.key}")
    private String taskKeySurveyAttention;

    @Value("${property.five9.default.campaign.state}")
    private String defaultCampaignState;
    private WhatsappSubjectProperties whatsappSubjectProperties;

    public ConversationMapper(WhatsappSubjectProperties whatsappSubjectProperties){
        this.whatsappSubjectProperties = whatsappSubjectProperties;
    }

    public Conversation whatsappIncomingRequestToConversation(Sessions session,
                                                              WhatsappIncomingMessageRequestDTO whatsappIncomingMessageRequestDTO){
        String messageText = Utils.processWhatsAppType(whatsappIncomingMessageRequestDTO);
        return Conversation.builder().conversationId(session.getConversationId())
                .clientId(whatsappIncomingMessageRequestDTO.getSubscriber().getIdentifier())
                .sessionId(Optional.ofNullable(session.getSessionId()).orElse(Constants.EMPTY_STRING))
                .imSubject(whatsappIncomingMessageRequestDTO.getSubject())
                .clientName(whatsappIncomingMessageRequestDTO.getUserInfo().getUserName())
                .messageText(messageText)
                .messageAttachment(Optional.ofNullable(whatsappIncomingMessageRequestDTO.getMessageContent().getAttachment())
                        .map(Attachment::getUrl).orElse(null))
                .location(whatsappIncomingMessageRequestDTO.getMessageContent().getLocation())
                .subjectId(session.getSubjectId())
                .cascadeId(whatsappSubjectProperties.getCascade().get(session.getSubjectId()))
                .status(EnumConversationStatus.IN_PROGRESS)
                .build();
    }

    public InferenceOutRequest conversationToInferenceOutRequestNewConversation(
            Conversation conversation){
        return InferenceOutRequest.builder().apiKey(apiKeyInferenceBot)
                .taskKey(taskKeyMainAttention)
                .sessionId(Optional.ofNullable(conversation.getSessionId()).orElse(Constants.EMPTY_STRING))
                .text(conversation.getMessageText())
                .userKey(Constants.EMPTY_STRING)
                .messageJson(
                        MessageJson.builder().userId(Constants.EMPTY_STRING)
                                .idRecipient(conversation.getClientId())
                                .channel(Constants.WHATSAPP_CHANNEL)
                                .clientName(conversation.getClientName())
                                .agent(Constants.EMPTY_STRING).build()
                )
                .build();
    }

    public InferenceOutRequest conversationToInferenceSurveyOutRequestNewConversation(
            Conversation conversation){
        return InferenceOutRequest.builder().apiKey(apiKeyInferenceBot)
                .taskKey(taskKeySurveyAttention)
                .sessionId(Optional.ofNullable(conversation.getSessionId()).orElse(Constants.EMPTY_STRING))
                .text(conversation.getMessageText())
                .userKey(Constants.EMPTY_STRING)
                .messageJson(
                        MessageJson.builder().userId(Constants.EMPTY_STRING)
                                .idRecipient(conversation.getClientId())
                                .channel(Constants.WHATSAPP_CHANNEL)
                                .clientName(conversation.getClientName())
                                .agent(conversation.getAgentSurvey()).build()
                )
                .build();
    }

    public Conversation five9MessageAddedRequestToConversation(ConversationMessageEventRequest conversationMessageEventRequest
            , Sessions incomingSession) {
        return Conversation.builder().conversationId(incomingSession.getConversationId())
                .sessionId(incomingSession.getSessionId())
                .status(EnumConversationStatus.TRANSFER_AGENT)
                .messageText(conversationMessageEventRequest.getText())
                .clientId(incomingSession.getIdentifier().split("-")[0])
                .imSubject(incomingSession.getIdentifier().split("-")[1])
                .subjectId(incomingSession.getSubjectId())
                .cascadeId(whatsappSubjectProperties.getCascade().get(incomingSession.getSubjectId()))
                .build();
    }

    public Conversation genericConversationReponse(Sessions incomingSession, String message) {
        return Conversation.builder().conversationId(incomingSession.getConversationId())
                .sessionId(incomingSession.getSessionId())
                .status(EnumConversationStatus.IN_PROGRESS)
                .messageResponse(message)
                .clientId(incomingSession.getIdentifier().split("-")[0])
                .imSubject(incomingSession.getIdentifier().split("-")[1])
                .subjectId(incomingSession.getSubjectId())
                .cascadeId(whatsappSubjectProperties.getCascade().get(incomingSession.getSubjectId()))
                .build();
    }

    public SendConversationMessageRequest conversationToFive9SendConversationMessage(Conversation conversation){
        return SendConversationMessageRequest.builder().messageType(Constants.WHATSAPP_CONTENT_TYPE_TEXT)
                .message(conversation.getMessageText())
                .externalId(conversation.getConversationId())
                .build();
    }

    public Five9AcknowledgeMessageRequest messageRequestIdToAcknowledgeMessage(String requestId){
        return Five9AcknowledgeMessageRequest.builder().messages(
                List.of(
                        AcknowledgeMessage.builder().messageId(requestId)
                        .type(EnumAcknowledgeType.DELIVERED).build()
                )
        ).build();
    }

    public Conversation conversationAddTransferAgentInformation(Conversation conversation
            , InferenceOutResponse inferenceOutResponse){
        conversation.setTransferAgentInformation(
                TransferAgentInformation.builder().agent(inferenceOutResponse.getMessageJson().getAgent())
                        .agentSkill(inferenceOutResponse.getMessageJson().getAgentSkill())
                        .email(inferenceOutResponse.getMessageJson().getEmail())
                        .campaign(inferenceOutResponse.getMessageJson().getCampaign())
                        .clientRequest(inferenceOutResponse.getMessageJson().getClientRequest())
                        .build()
        );
        return conversation;
    }

    public Conversation conversationAddTransferAgentInformationState(Conversation conversation){
        conversation.setTransferAgentInformation(
                TransferAgentInformation.builder()
                        .campaign(defaultCampaignState)
                        .clientRequest(conversation.getMessageText())
                        .build()
        );
        return conversation;
    }

    public WhatsappDailyConversationHistory conversationMessageEventToWhatsappHistory(
            ConversationMessageEventRequest conversationMessageEventRequest, Sessions incomingSession){
        return WhatsappDailyConversationHistory.builder().conversationId(incomingSession.getConversationId())
                .sender(conversationMessageEventRequest.getDisplayName())
                .receiver(incomingSession.getIdentifier().split("-")[0])
                .type(Constants.OUTBOUND_TYPE)
                .content(conversationMessageEventRequest.getText())
                .build();
    }

    public ConversationCountSummary createNewBotConversationCountSummary(Conversation conversation){
        return ConversationCountSummary.builder().conversationId(conversation.getConversationId())
                .idClient(conversation.getClientId())
                .agent(Constants.INFERENCE_BOT_RECEIVER)
                .channel(Constants.WHATSAPP_CHANNEL)
                .countClient(Constants.DEFAULT_SUMMARY_COUNT_INITIALIZER)
                .countAgent(Constants.DEFAULT_SUMMARY_COUNT_INITIALIZER)
                .countTotal(Constants.DEFAULT_SUMMARY_TOTAL_COUNT_INITIALIZER)
                .status(EnumSummaryStatus.IN_PROGRESS).build();
    }

    public ConversationCountSummary createNewFive9ConversationCountSummary(Conversation conversation){
        return ConversationCountSummary.builder().conversationId(conversation.getConversationId())
                .idClient(conversation.getClientId())
                .agent(Constants.FIVE9_RECEIVER)
                .channel(Constants.WHATSAPP_CHANNEL)
                .countClient(Constants.DEFAULT_FIVE9_SUMMARY_COUNT_INITIALIZER)
                .countAgent(Constants.DEFAULT_FIVE9_SUMMARY_COUNT_INITIALIZER)
                .countTotal(Constants.DEFAULT_FIVE9_SUMMARY_COUNT_INITIALIZER)
                .status(EnumSummaryStatus.IN_PROGRESS).build();
    }

}
