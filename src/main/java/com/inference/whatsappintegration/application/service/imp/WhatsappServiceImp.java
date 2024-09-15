package com.inference.whatsappintegration.application.service.imp;

import com.inference.whatsappintegration.application.dto.conversationsdto.five9conversationsendmessagerequest.SendConversationMessageRequest;
import com.inference.whatsappintegration.application.dto.whatsappinrequest.WhatsappIncomingMessageRequestDTO;
import com.inference.whatsappintegration.application.mapper.ConversationMapper;
import com.inference.whatsappintegration.application.mapper.WhatsappMapper;
import com.inference.whatsappintegration.domain.model.Conversation;
import com.inference.whatsappintegration.domain.service.*;
import com.inference.whatsappintegration.infrastructure.config.mdc.MdcAwareExecutor;
import com.inference.whatsappintegration.infrastructure.exception.ErrorMessages;
import com.inference.whatsappintegration.infrastructure.exception.GenericException;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Five9Session;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;
import com.inference.whatsappintegration.infrastructure.persistence.repository.Five9SessionRepository;
import com.inference.whatsappintegration.infrastructure.persistence.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.inference.whatsappintegration.util.Utils;
import com.inference.whatsappintegration.util.Constants;

import java.util.Optional;

@Service
public class WhatsappServiceImp implements WhatsappService {

    private static final Logger LOGGER  = LoggerFactory.getLogger(WhatsappServiceImp.class);

    private WhatsappMapper whatsappMapper;
    private ConversationMapper conversationMapper;
    private SessionRepository sessionRepository;
    private ConversationService conversationService;
    private Five9SessionRepository five9SessionRepository;
    private Five9ConversationService five9ConversationService;
    private DatabaseOperationsService databaseOperationsService;
    private MdcAwareExecutor mdcAwareExecutor;
    private RedisService redisService;

    @Value("${property.inference.bot.disable}")
    private int botFlag;

    public WhatsappServiceImp(WhatsappMapper whatsappMapper,ConversationMapper conversationMapper,
                              SessionRepository sessionRepository, ConversationService conversationService,
                              Five9SessionRepository five9SessionRepository, Five9ConversationService five9ConversationService,
                              DatabaseOperationsService databaseOperationsService, MdcAwareExecutor mdcAwareExecutor,
                              RedisService redisService){
        this.whatsappMapper = whatsappMapper;
        this.conversationMapper = conversationMapper;
        this.sessionRepository = sessionRepository;
        this.conversationService = conversationService;
        this.five9SessionRepository = five9SessionRepository;
        this.five9ConversationService = five9ConversationService;
        this.databaseOperationsService = databaseOperationsService;
        this.mdcAwareExecutor = mdcAwareExecutor;
        this.redisService = redisService;
    }

    public void processReceiveInteraction(WhatsappIncomingMessageRequestDTO whatsappIncomingMessageRequestDTO){
        LOGGER.info("Starting process receive interaction");
        try {
            Utils.logAsJson(LOGGER, whatsappIncomingMessageRequestDTO, "WhatsappIncomingMessageRequestDTO");
            Sessions incomingSession = getSessionFromRequest(whatsappIncomingMessageRequestDTO);
            Conversation conversation = conversationMapper
                    .whatsappIncomingRequestToConversation(incomingSession, whatsappIncomingMessageRequestDTO);
            Conversation tmpConversation = conversation;
            mdcAwareExecutor.execute(() -> databaseOperationsService.insertClientInteraction(tmpConversation, incomingSession));
            conversation = conversationService.processDirectSubjectTransfer(conversation);
            if ((botFlag == Constants.BOT_FLAG_DEACTIVATED && incomingSession.getSessionId() == null) ||
                    (conversation.getTransferAgentInformation() != null && incomingSession.getSessionId() == null)){
                handleBotDeactivatedAndSessionIsNotPresent(conversation, incomingSession);
            } else if (botFlag == Constants.BOT_FLAG_DEACTIVATED || conversation.getTransferAgentInformation() != null){
                handleBotDeactivatedAndSessionIsPresent(conversation, incomingSession);
            } else {
                switch (incomingSession.getChannelType()){
                    case Constants.CHANNEL_TYPE_INFERENCE_BOT:
                        processBotInteraction(conversation, incomingSession);
                        break;
                    case Constants.CHANNEL_TYPE_FIVE9_AGENT:
                        processAgentInteraction(conversation, incomingSession);
                        break;
                    case Constants.CHANNEL_TYPE_SURVEY:
                        processSurveyBotInteraction(conversation, incomingSession);
                        break;
                    default:
                        throw new GenericException("No channel type session matching");
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Exception while processing recieve interaction: {}", ex.getMessage());
        }  finally {
            LOGGER.info("Finish process receive interaction");
            Utils.clearMDCParameters();
        }

    }

    private void processBotInteraction(Conversation conversation, Sessions incomingSession) {
        LOGGER.info("Starting process bot interaction");
        try {
            conversation = conversationService.sendConversationToInferenceBot(conversation);
            Conversation tempConversation = conversation;
            mdcAwareExecutor.execute(() -> databaseOperationsService.insertBotMetricsInteraction(tempConversation));
            switch (conversation.getStatus()) {
                case IN_PROGRESS:
                    if (incomingSession.getSessionId() == null) {
                        incomingSession.setSessionId(conversation.getSessionId());
                    }
                    redisService.saveWithAudit(incomingSession, Constants.EXPIRATION_TIME_DEFAULT);
                    conversationService.sendConversationWhatsappResponse(conversation);
                    break;
                case TRANSFER_AGENT:
                    incomingSession.setChannelType(Constants.CHANNEL_TYPE_FIVE9_AGENT);
                    incomingSession.setSessionId(null);
                    redisService.saveWithAudit(incomingSession, null);
                    if (conversation.getTransferAgentInformation() != null) {
                        conversationService.sendConversationWhatsappResponse(conversation);
                    }
                    conversationService.sendConversationToFive9Agent(conversation, incomingSession);
                    break;
                case TERMINATED:
                    sessionRepository.delete(incomingSession);
                    conversationService.sendConversationWhatsappResponse(conversation);
                    break;
                default:
                    throw new GenericException("No conversation status matching");
            }
        } catch (Exception ex) {
            LOGGER.error("Exception while processing bot interaction: {}", ex.getMessage());
        }  finally {
            LOGGER.info("Finish process bot interaction");
        }
    }

    private void processAgentInteraction(Conversation conversation, Sessions incomingSession){
        LOGGER.info("Starting process agent interaction");
        try {
            Five9Session five9Session = five9SessionRepository.findById(incomingSession.getSessionId()).orElseThrow(
                    () -> new GenericException(ErrorMessages.FIVE9_SESSION_NOT_PRESENT));
            mdcAwareExecutor.execute(() -> databaseOperationsService.processClientSummaryInteraction(conversation));
            SendConversationMessageRequest sendConversationMessageRequest = conversationMapper.conversationToFive9SendConversationMessage(conversation);
            if(!sendConversationMessageRequest.getMessage().isEmpty()){
                int successCode = five9ConversationService.sendFive9MessageConversation(five9Session, sendConversationMessageRequest);
                if (successCode == 1){
                    LOGGER.info("Retrying to send five 9 message, generating new token and session");
                    conversation.setTransferAgentInformation(five9Session.getTransferAgentInformation());
                    five9SessionRepository.delete(five9Session);
                    incomingSession.setSessionId(null);
                    sessionRepository.save(incomingSession);
                    conversationService.sendConversationToFive9Agent(conversation, incomingSession);
                }
            } else {
                LOGGER.info("Message blank!");
            }
        } catch (Exception ex) {
            LOGGER.error("Exception while processing agent interaction: {}", ex.getMessage());
        } finally {
            LOGGER.info("Finish process agent interaction");
        }
    }

    private void processSurveyBotInteraction(Conversation conversation, Sessions incomingSession){
        LOGGER.info("Starting process bot survey flow interaction");
        try {
            conversation.setAgentSurvey(incomingSession.getAgentName());
            conversation = conversationService.sendConversationToInferenceSurveyBot(conversation);

            Conversation tempConversation = conversation;
            mdcAwareExecutor.execute(() -> databaseOperationsService.insertBotMetricsInteraction(tempConversation));
            switch (conversation.getStatus()) {
                case IN_PROGRESS:
                    if (incomingSession.getSessionId() == null) {
                        incomingSession.setSessionId(conversation.getSessionId());
                    }
                    redisService.saveWithAudit(incomingSession, Constants.EXPIRATION_TIME_DEFAULT);
                    break;
                case TERMINATED:
                    sessionRepository.delete(incomingSession);
                    break;
                default:
                    throw new GenericException("No conversation status matching");
            }
            conversationService.sendConversationWhatsappResponse(conversation); // if credit expired check message
        } catch (Exception ex) {
            LOGGER.error("Exception while processing bot survey flow interaction: {}", ex.getMessage());
        }  finally {
            LOGGER.info("Finish process bot survey flow interaction");
        }
    }

    private Sessions getSessionFromRequest(WhatsappIncomingMessageRequestDTO whatsappIncomingMessageRequestDTO) {
        Sessions incomingSession = whatsappMapper.whatsappReceiveDTOtoSession(whatsappIncomingMessageRequestDTO);
        return validateSession(incomingSession);
    }

    private Sessions getSessionFromConversation(Conversation conversation) {
        Sessions incomingSession = whatsappMapper.whatsappSessionFromConversation(conversation);
        return validateSession(incomingSession);
    }

    private void handleBotDeactivatedAndSessionIsNotPresent(Conversation conversation, Sessions incomingSession){
        LOGGER.info("Sending interaction to Five9 Agent bot deactivated WO-Session or subject is direct transfer");
        try {
            if (incomingSession.getChannelType() == Constants.CHANNEL_TYPE_INFERENCE_BOT){
                incomingSession.setChannelType(Constants.CHANNEL_TYPE_FIVE9_AGENT);
                redisService.saveWithAudit(incomingSession, null);
            }
            conversationService.sendConversationToFive9Agent(conversation, incomingSession);
        } catch (Exception ex) {
            LOGGER.error("Exception while processing bot deactivated and sessionId not present: {}", ex.getMessage());
        } finally {
            LOGGER.info("Finish process bot deactivated and sessionId not present");
        }
    }

    private void handleBotDeactivatedAndSessionIsPresent(Conversation conversation, Sessions incomingSession){
        LOGGER.info("Sending interaction to Five9 Agent bot deactivated W-Session or subject is direct transfer");
        try {
            if (five9SessionRepository.findById(incomingSession.getSessionId()).isEmpty()){
                conversationService.sendConversationToFive9Agent(conversation, incomingSession);
            } else {
                processAgentInteraction(conversation, incomingSession);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception while processing bot deactivated and sessionId present: {}", ex.getMessage());
        } finally {
            LOGGER.info("Finish process bot deactivated and sessionId present");
        }
    }

    public Sessions validateSession(Sessions incomingSession){
        Optional<Sessions> session = sessionRepository.findById(incomingSession.getIdentifier());

        if (session.isEmpty()) {
            LOGGER.info("Session not found generating one");
            incomingSession.setConversationId(Utils.generateConversationId());
            incomingSession.setChannelType(Constants.CHANNEL_TYPE_INFERENCE_BOT);
            redisService.saveWithAudit(incomingSession, Constants.EXPIRATION_TIME_DEFAULT);
        } else {
            LOGGER.info("Session found");
            incomingSession = session.get();
        }
        Utils.setMDCParameters(incomingSession);
        return incomingSession;
    }

}
