package com.inference.whatsappintegration.application.mapper;

import com.inference.whatsappintegration.application.dto.conversationsdto.five9createconversationrequest.Attributes;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9createconversationrequest.Contact;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9createconversationrequest.Five9CreateConversationRequest;
import com.inference.whatsappintegration.application.dto.five9tokenrequest.Five9TokenRequest;
import com.inference.whatsappintegration.application.dto.five9tokenresponse.ApiUrl;
import com.inference.whatsappintegration.application.dto.five9tokenresponse.DataCenter;
import com.inference.whatsappintegration.application.dto.five9tokenresponse.Five9TokenResponse;
import com.inference.whatsappintegration.application.dto.whatappoutrequest.*;
import com.inference.whatsappintegration.application.dto.whatsappinrequest.WhatsappIncomingMessageRequestDTO;
import com.inference.whatsappintegration.domain.model.Conversation;
import com.inference.whatsappintegration.domain.model.TransferAgentInformation;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Five9Session;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;
import com.inference.whatsappintegration.infrastructure.persistence.entity.WhatsappDailyConversationHistory;
import com.inference.whatsappintegration.util.Constants;
import com.inference.whatsappintegration.util.Utils;
import com.inference.whatsappintegration.util.enums.EnumConversationStatus;
import com.inference.whatsappintegration.util.enums.EnumWhatsappContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class WhatsappMapper {

    @Value("${property.server.five9.callback.ip}")
    private String serverNgrokValue;
    @Value("${property.five9.default.campaign.option}")
    private String defaultFive9Campaign;
    @Value("${property.redis.defaultExpirationTime}")
    private long defaultExpirationTime;

    public Sessions whatsappReceiveDTOtoSession(WhatsappIncomingMessageRequestDTO whatsappIncomingMessageRequestDTO){
        return Sessions.builder().identifier(whatsappIncomingMessageRequestDTO.getSubscriber().getIdentifier() + "-"
                + whatsappIncomingMessageRequestDTO.getSubject())
                .subjectId(whatsappIncomingMessageRequestDTO.getSubjectId().toString())
                .expiration(defaultExpirationTime).build();
    }

    public Sessions whatsappSessionFromConversation(Conversation conversation){
        return Sessions.builder().identifier(conversation.getClientId() + "-"
                        + conversation.getImSubject())
                .subjectId(conversation.getSubjectId())
                .expiration(defaultExpirationTime).build();
    }

    public List<WhatsappMessage> conversationToWhatsappTextMessage(Conversation conversation){
        List<WhatsappMessage> listWhatsAppMessages = new ArrayList<>();
        String textMessage = conversation.getStatus() == EnumConversationStatus.TRANSFER_AGENT ?
                conversation.getMessageText() :
                conversation.getMessageResponse();

        textMessage = Utils.replaceSpecialCharacters(textMessage);
        List<String> listTextMessages = Utils.splitSpecialMessageLinks(textMessage);

        for (String textMessageIterate : listTextMessages) {
            WhatsappContent whatsappContent = createWhatsappContent(textMessageIterate);
            listWhatsAppMessages.add(buildWhatsAppmessage(conversation, whatsappContent));
        }
        return listWhatsAppMessages;
    }

    public WhatsappDailyConversationHistory conversationClientToWhatsappHistory(Conversation conversation,
                                                                                Sessions incomingSession){
        return WhatsappDailyConversationHistory.builder().conversationId(conversation.getConversationId())
                .sender(conversation.getClientId())
                .receiver(incomingSession.getChannelType() == Constants.CHANNEL_TYPE_FIVE9_AGENT ?
                        Constants.FIVE9_RECEIVER : Constants.INFERENCE_BOT_RECEIVER)
                .type(Constants.INBOUND_TYPE)
                .content(conversation.getMessageText())
                .build();
    }

    public WhatsappDailyConversationHistory conversationBotToWhatsappHistory(Conversation conversation){
        String messageBotResponse = conversation.getStatus() == EnumConversationStatus.TRANSFER_AGENT ?
                conversation.getMessageText() : conversation.getMessageResponse();

        return WhatsappDailyConversationHistory.builder().conversationId(conversation.getConversationId())
                .sender(Constants.INFERENCE_BOT_RECEIVER)
                .receiver(conversation.getClientId())
                .type(Constants.OUTBOUND_TYPE)
                .content(messageBotResponse)
                .build();
    }

    public Five9Session five9TokenResponseToFive9Session(Five9TokenResponse five9TokenResponse, Sessions incomingSession,
                                                         Conversation conversation){
        Optional<DataCenter> dataCenterOpt = five9TokenResponse.getMetadata().getDataCenters().stream().findFirst();
        Optional<ApiUrl> apiUrlOpt = dataCenterOpt.flatMap(dataCenter -> dataCenter.getApiUrls().stream().findFirst());

        return Five9Session.builder().tokenId(five9TokenResponse.getTokenId())
                .farmId(five9TokenResponse.getContext().getFarmId())
                .host(apiUrlOpt.map(ApiUrl::getHost).orElse(Constants.DEFAULT_FIVE9_API_HOST))
                .identifier(incomingSession.getIdentifier())
                .transferAgentInformation(conversation.getTransferAgentInformation())
                .build();
    }

    public Five9CreateConversationRequest five9TokenResponseToCreateConversationRequest(Conversation conversation, Five9TokenResponse five9TokenResponse){
        String clientRequest = conversation.getTransferAgentInformation() != null ? conversation
                .getTransferAgentInformation().getClientRequest() : conversation.getMessageText();

        return Five9CreateConversationRequest.builder()
                .campaignName(Optional.ofNullable(conversation.getTransferAgentInformation())
                        .map(TransferAgentInformation::getCampaign)
                        .orElse(defaultFive9Campaign)) //CHANGE CAMPAIGN DEFAULT
                .tenantId(Integer.parseInt(five9TokenResponse.getOrgId()))
                .type(Constants.WHATSAPP_CHANNEL)
                .priority(Constants.DEFAULT_FIVE9_PRIORITY)
                .callbackUrl(serverNgrokValue.concat(Constants.DEFAULT_FIVE9_CONTEXT_PATH))
                .contact(
                        Contact.builder().number1(conversation.getClientId())
                                .firstName(conversation.getClientName())
                                .socialAccountImageUrl(Constants.DEFAULT_FIVE9_WHATSAPP_IMAGE_PROFILE)
                                .socialAccountProfileUrl(Constants.DEFAULT_FIVE9_WHATSAPP_IMAGE_PROFILE)
                                .build()
                )
                .attributes(
                        Attributes.builder()
                                .question(clientRequest)
                                .agentSkill(Optional.ofNullable(conversation.getTransferAgentInformation())
                                        .map(TransferAgentInformation::getAgentSkill)
                                        .orElse(Constants.EMPTY_STRING)
                                )
                                .agentHSM(Optional.ofNullable(conversation.getTransferAgentInformation())
                                        .map(TransferAgentInformation::getAgent)
                                        .orElse(Constants.EMPTY_STRING)
                                )
                                .build()

                ).build();
    }

    public Five9TokenRequest createFive9TokenRequest (){
        return Five9TokenRequest.builder().tenantName(Constants.SOLE_TENANT_NAME).build();
    }

    private WhatsappContent createWhatsappContent(String textMessage) {
        WhatsappContent.WhatsappContentBuilder whatsappContentBuilder = WhatsappContent.builder();
        String matchingContentType = Utils.findMatchingContentType(textMessage);

        if (matchingContentType != null) {
            EnumWhatsappContentType contentType = EnumWhatsappContentType.fromFieldName(matchingContentType);
            Attachment attachment = Attachment.builder()
                    .url(Utils.extractUrlFromMessage(textMessage)).build();

            whatsappContentBuilder.contentType(contentType.getContentType()).attachment(attachment);
        } else {
            whatsappContentBuilder.contentType(EnumWhatsappContentType.TEXT.getContentType()).text(textMessage);
        }

        return whatsappContentBuilder.build();
    }

    private WhatsappMessage buildWhatsAppmessage(Conversation conversation, WhatsappContent whatsappContent){
        return WhatsappMessage.builder().requestId(Utils.generateRandomUUID())
                .cascadeId(conversation.getCascadeId())
                .subscriberFilter(
                        SubscriberFilter.builder().address(conversation.getClientId())
                                .type(Constants.SUBSCRIBE_FILTER_TYPE_PHONE).build()
                )
                .content(Content.builder().whatsappContent(whatsappContent).build())
                .build();
    }
}
