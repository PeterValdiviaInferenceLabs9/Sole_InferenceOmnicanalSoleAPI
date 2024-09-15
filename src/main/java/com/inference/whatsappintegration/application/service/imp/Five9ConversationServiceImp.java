package com.inference.whatsappintegration.application.service.imp;

import com.inference.whatsappintegration.application.dto.conversationsdto.five9acknowledgemessagerequest.Five9AcknowledgeMessageRequest;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9conversationsendmessagerequest.SendConversationMessageRequest;
import com.inference.whatsappintegration.application.dto.conversationseventsdto.conversationmessageeventrequest.ConversationMessageEventRequest;
import com.inference.whatsappintegration.application.dto.conversationseventsdto.conversationterminateevent.ConversationTerminateEvenRequest;
import com.inference.whatsappintegration.application.mapper.ConversationMapper;
import com.inference.whatsappintegration.domain.model.Conversation;
import com.inference.whatsappintegration.domain.service.*;
import com.inference.whatsappintegration.infrastructure.config.mdc.MdcAwareExecutor;
import com.inference.whatsappintegration.infrastructure.exception.GenericException;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Five9Session;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;
import com.inference.whatsappintegration.infrastructure.persistence.repository.Five9SessionRepository;
import com.inference.whatsappintegration.infrastructure.persistence.repository.SessionRepository;
import com.inference.whatsappintegration.util.Constants;
import com.inference.whatsappintegration.util.DefaultMessages;
import com.inference.whatsappintegration.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Five9ConversationServiceImp implements Five9ConversationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Five9ConversationServiceImp.class);
    private ConversationMapper conversationMapper;
    private SessionRepository sessionRepository;
    private ConversationService conversationService;
    private Five9SessionRepository five9SessionRepository;
    private Five9Service five9Service;
    private DatabaseOperationsService databaseOperationsService;
    private MdcAwareExecutor mdcAwareExecutor;
    private RedisService redisService;
    @Value("${property.inference.bot.survey.disable}")
    private int botSurveyFlag;

    public Five9ConversationServiceImp(ConversationMapper conversationMapper,
                                       SessionRepository sessionRepository, ConversationService conversationService,
                                       Five9SessionRepository five9SessionRepository, Five9Service five9Service,
                                       DatabaseOperationsService databaseOperationsService, MdcAwareExecutor mdcAwareExecutor,
                                       RedisService redisService){
        this.conversationMapper = conversationMapper;
        this.sessionRepository = sessionRepository;
        this.conversationService = conversationService;
        this.five9SessionRepository = five9SessionRepository;
        this.five9Service = five9Service;
        this.databaseOperationsService = databaseOperationsService;
        this.mdcAwareExecutor = mdcAwareExecutor;
        this.redisService = redisService;
    }

    public void processFive9MessageAdded(String tokenId, ConversationMessageEventRequest conversationMessageEventRequest){
        LOGGER.info("Processing message added request");
        try{
            Utils.logAsJson(LOGGER, conversationMessageEventRequest, "ConversationMessageEventRequest");
            Optional<Pair<Five9Session, Sessions>> optionalSessions = verifySessions(tokenId);
            if (optionalSessions.isEmpty()) {
                return;
            }
            Five9Session five9Session = optionalSessions.get().getFirst();
            Sessions incomingSession = optionalSessions.get().getSecond();

            Utils.setMDCParameters(incomingSession);
            mdcAwareExecutor.execute(() -> databaseOperationsService.insertFive9Interaction(conversationMessageEventRequest, incomingSession));

            Conversation conversation = conversationMapper.five9MessageAddedRequestToConversation(conversationMessageEventRequest
                    , incomingSession);
            mdcAwareExecutor.execute(() -> {
                try {
                    conversationService.sendConversationWhatsappResponse(conversation);
                    databaseOperationsService.processFive9CountSummaryInteraction(conversation);
                } catch (InterruptedException e) {
                    throw new GenericException("Error while sending whatsapp response in thread");
                }
            });
            Five9AcknowledgeMessageRequest five9AcknowledgeMessageRequest = conversationMapper
                    .messageRequestIdToAcknowledgeMessage(conversationMessageEventRequest.getMessageId());
            if (!conversation.getMessageText().equals(Constants.DEFAULT_MESSAGE_TERMINATION)){
                five9Service.sendAcknowledgeMessageToConversation(five9Session, five9AcknowledgeMessageRequest);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception while message added event: {}", ex.getMessage());
        } finally {
            LOGGER.info("Finish processing message added event");
            Utils.clearMDCParameters();
        }

    }

    public void processFive9Terminate(String tokenId, ConversationTerminateEvenRequest conversationTerminateEvenRequest){
        LOGGER.info("Processing conversation terminate request");
        try {
            Utils.logAsJson(LOGGER, conversationTerminateEvenRequest, "ConversationTerminateEvenRequest");
            Optional<Pair<Five9Session, Sessions>> optionalSessions = verifySessions(tokenId);
            if (optionalSessions.isEmpty()) {
                return;
            }
            Pair<Five9Session, Sessions> sessions = optionalSessions.get();
            five9SessionRepository.delete(sessions.getFirst());
            Sessions incomingSession = sessions.getSecond();
            Utils.setMDCParameters(incomingSession);

            if (botSurveyFlag == Constants.ONE || Constants.SUBJECT_ID_NO_SURVEY.contains(incomingSession.getSubjectId())){
                sessionRepository.delete(incomingSession);
            } else {
                incomingSession.setSessionId(null);
                incomingSession.setAgentName(conversationTerminateEvenRequest.getDisplayName());
                incomingSession.setChannelType(Constants.CHANNEL_TYPE_SURVEY);
                incomingSession.setExpiration(Constants.EXPIRATION_TIME_SURVEY);
                redisService.saveWithAudit(incomingSession, Constants.EXPIRATION_TIME_SURVEY);
                Thread.sleep(500);
                sendWhatsAppConversationSurveyMessage(incomingSession);
            }
            mdcAwareExecutor.execute(() -> databaseOperationsService.processFive9CountSummaryTerminateInteraction(incomingSession.getConversationId()));
            LOGGER.info("Session flow updated to survey bot");
        } catch (Exception ex) {
            LOGGER.error("Exception while processing terminate event: {}", ex.getMessage());
        } finally {
            LOGGER.info("Finish processing terminate event");
            Utils.clearMDCParameters();
        }
    }

    public int sendFive9MessageConversation(Five9Session five9Session
            , SendConversationMessageRequest sendConversationMessageRequest){
        return five9Service.sendFive9ConversationMessage(five9Session, sendConversationMessageRequest);
    }

    private void sendWhatsAppConversationSurveyMessage(Sessions incomingSession){
        LOGGER.info("Sending survey start message");
        try{
            Conversation conversation = conversationMapper.genericConversationReponse(incomingSession, DefaultMessages.DEFAULT_START_SURVEY);
            conversationService.sendConversationWhatsappResponse(conversation);
            conversation.setMessageResponse(DefaultMessages.DEFAULT_FIRST_MESSAGE);
            conversationService.sendConversationWhatsappResponse(conversation);
        }catch (Exception ex) {
            LOGGER.error("Exception while sending survey start message: {}", ex.getMessage());
        } finally {
            LOGGER.info("Finish sending survey start message");
        }
    }

    private Optional<Pair<Five9Session, Sessions>> verifySessions(String tokenId){
        Optional<Five9Session> optionalFive9Session = five9SessionRepository.findById(tokenId);
        if (optionalFive9Session.isEmpty()) {
            LOGGER.info("Token expired for Five9Session with id {}", tokenId);
            return Optional.empty();
        }

        Five9Session five9Session = optionalFive9Session.get();
        Optional<Sessions> optionalIncomingSession = sessionRepository.findById(five9Session.getIdentifier());

        if (optionalIncomingSession.isEmpty()) {
            LOGGER.info("Token expired for Session with id {}", five9Session.getIdentifier());
            return Optional.empty();
        }

        return Optional.of(Pair.of(five9Session, optionalIncomingSession.get()));
    }

}
