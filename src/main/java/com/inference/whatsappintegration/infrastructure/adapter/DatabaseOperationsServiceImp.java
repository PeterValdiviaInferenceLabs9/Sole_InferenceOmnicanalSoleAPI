package com.inference.whatsappintegration.infrastructure.adapter;

import com.inference.whatsappintegration.application.dto.conversationseventsdto.conversationmessageeventrequest.ConversationMessageEventRequest;
import com.inference.whatsappintegration.application.mapper.ConversationMapper;
import com.inference.whatsappintegration.application.mapper.WhatsappMapper;
import com.inference.whatsappintegration.domain.model.Conversation;
import com.inference.whatsappintegration.domain.service.DatabaseOperationsService;
import com.inference.whatsappintegration.infrastructure.persistence.entity.ConversationCountSummary;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;
import com.inference.whatsappintegration.infrastructure.persistence.entity.WhatsappDailyConversationHistory;
import com.inference.whatsappintegration.infrastructure.persistence.repository.ConversationCountSummaryRepository;
import com.inference.whatsappintegration.infrastructure.persistence.repository.WhatsappDailyConversationHistoryRepository;
import com.inference.whatsappintegration.util.Constants;
import com.inference.whatsappintegration.util.enums.EnumSummaryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;


@Component
public class DatabaseOperationsServiceImp implements DatabaseOperationsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseOperationsServiceImp.class);
    private WhatsappDailyConversationHistoryRepository whatsappDailyConversationHistoryRepository;
    private ConversationCountSummaryRepository conversationCountSummaryRepository;
    private WhatsappMapper whatsappMapper;
    private ConversationMapper conversationMapper;

    public DatabaseOperationsServiceImp(WhatsappDailyConversationHistoryRepository whatsappDailyConversationHistoryRepository,
                                        WhatsappMapper whatsappMapper, ConversationMapper conversationMapper,
                                        ConversationCountSummaryRepository conversationCountSummaryRepository) {
        this.whatsappDailyConversationHistoryRepository = whatsappDailyConversationHistoryRepository;
        this.whatsappMapper = whatsappMapper;
        this.conversationMapper = conversationMapper;
        this.conversationCountSummaryRepository = conversationCountSummaryRepository;
    }

    public void insertBotMetricsInteraction(Conversation conversation){
        LOGGER.info("Inserting metrics in async method");
        insertBotInteraction(conversation);
        processBotCountSummary(conversation);
    }

    private void insertBotInteraction(Conversation conversation){
        WhatsappDailyConversationHistory whatsappDailyConversationHistory = whatsappMapper
                .conversationBotToWhatsappHistory(conversation);
        insertWhatsAppInteraction(whatsappDailyConversationHistory);
    }

    public void insertClientInteraction(Conversation conversation, Sessions incomingSession){
        WhatsappDailyConversationHistory whatsappDailyConversationHistory = whatsappMapper
                .conversationClientToWhatsappHistory(conversation, incomingSession);
        insertWhatsAppInteraction(whatsappDailyConversationHistory);
    }

    public void insertFive9Interaction(ConversationMessageEventRequest conversationMessageEventRequest, Sessions incomingSession){
        WhatsappDailyConversationHistory whatsappDailyConversationHistory = conversationMapper
                .conversationMessageEventToWhatsappHistory(conversationMessageEventRequest, incomingSession);
        insertWhatsAppInteraction(whatsappDailyConversationHistory);
    }

    private void insertWhatsAppInteraction(WhatsappDailyConversationHistory whatsappDailyConversationHistory){
        LOGGER.info("Inserting whatsapp interaction");
        try{
            whatsappDailyConversationHistoryRepository.save(whatsappDailyConversationHistory);
        } catch (Exception exception) {
            LOGGER.error("Error while executing insert in whatsapp interaction {}", exception.getMessage());
        } finally {
            LOGGER.info("Whatsapp interaction log successfully inserted");
        }
    }

    @Transactional
    public void processBotCountSummary(Conversation conversation){
        LOGGER.info("Starting process bot count summary");
        try{
            Optional<ConversationCountSummary> conversationCountSummaryOpt = conversationCountSummaryRepository
                    .findTopByConversationIdAndStatusOrderByLastUpdatedAtDesc(conversation.getConversationId(), EnumSummaryStatus.IN_PROGRESS);
            ConversationCountSummary conversationCountSummary;
            if (conversationCountSummaryOpt.isEmpty()){
                conversationCountSummary = conversationMapper
                        .createNewBotConversationCountSummary(conversation);
                conversationCountSummaryRepository.save(conversationCountSummary);
            } else {
                conversationCountSummary = conversationCountSummaryOpt.get();
                conversationCountSummary.setCountAgent(conversationCountSummary.getCountAgent() + Constants.ONE);
                conversationCountSummary.setCountClient(conversationCountSummary.getCountClient() + Constants.ONE);
                conversationCountSummary.setCountTotal(conversationCountSummary.getCountTotal() + Constants.TWO);
                switch (conversation.getStatus()){
                    case TERMINATED:
                        conversationCountSummary.setStatus(EnumSummaryStatus.SELF_MANAGEMENT);
                        break;
                    case TRANSFER_AGENT:
                        conversationCountSummary.setStatus(EnumSummaryStatus.TRANSFER_AGENT);
                        ConversationCountSummary conversationFive9CountSummary = conversationMapper
                                .createNewFive9ConversationCountSummary(conversation);
                        conversationCountSummaryRepository.save(conversationFive9CountSummary);
                        break;
                    default:
                        break;

                }
                conversationCountSummaryRepository.save(conversationCountSummary);
            }


        } catch (Exception exception) {
            LOGGER.error("Error while executing processing count summary {}", exception.getMessage());
        } finally {
            LOGGER.info("Process count summary successfully process");
        }
    }

    @Transactional
    public void processClientSummaryInteraction(Conversation conversation){
        LOGGER.info("Starting process client count summary");
        try {
            Optional<ConversationCountSummary> conversationCountSummaryOpt = conversationCountSummaryRepository
                    .findTopByConversationIdAndStatusOrderByLastUpdatedAtDesc(conversation.getConversationId(),
                            EnumSummaryStatus.IN_PROGRESS);
            ConversationCountSummary conversationCountSummary;
            if (conversationCountSummaryOpt.isPresent()) {
                conversationCountSummary = conversationCountSummaryOpt.get();
                conversationCountSummary.setCountClient(conversationCountSummary.getCountClient() + Constants.ONE);
                conversationCountSummary.setCountTotal(conversationCountSummary.getCountTotal() + Constants.ONE);
            } else {
                LOGGER.error("Client summary cannot be updated because row doesn't exists");
                conversationCountSummary = conversationMapper.createNewFive9ConversationCountSummary(conversation);
            }
            conversationCountSummaryRepository.save(conversationCountSummary);

        } catch (Exception exception) {
            LOGGER.error("Error while executing process client count summary {}", exception.getMessage());
        } finally {
            LOGGER.info("Process client count summary successfully process");
        }
    }

    @Transactional
    public void processFive9CountSummaryInteraction(Conversation conversation){
        LOGGER.info("Starting process five9 count summary");
        try {
            Optional<ConversationCountSummary> conversationCountSummaryOpt = conversationCountSummaryRepository
                    .findTopByConversationIdAndStatusOrderByLastUpdatedAtDesc(conversation.getConversationId(),
                            EnumSummaryStatus.IN_PROGRESS);
            ConversationCountSummary conversationCountSummary;
            if (conversationCountSummaryOpt.isPresent()) {
                conversationCountSummary = conversationCountSummaryOpt.get();
                conversationCountSummary.setCountAgent(conversationCountSummary.getCountAgent() + Constants.ONE);
                conversationCountSummary.setCountTotal(conversationCountSummary.getCountTotal() + Constants.ONE);
            } else {
                LOGGER.error("Five9 summary count cannot be updated because row doesn't exists");
                conversationCountSummary = conversationMapper.createNewFive9ConversationCountSummary(conversation);
            }
            conversationCountSummaryRepository.save(conversationCountSummary);

        } catch (Exception exception) {
            LOGGER.error("Error while executing process five9 count summary {}", exception.getMessage());
        } finally {
            LOGGER.info("Process five9 count summary successfully process");
        }
    }

    @Transactional
    public void processFive9CountSummaryTerminateInteraction(String conversationId){
        LOGGER.info("Starting process five9 terminate count summary");
        try {
            Optional<ConversationCountSummary> conversationCountSummaryOpt = conversationCountSummaryRepository
                    .findTopByConversationIdAndStatusOrderByLastUpdatedAtDesc(conversationId,
                            EnumSummaryStatus.IN_PROGRESS);
            ConversationCountSummary conversationCountSummary;
            if (conversationCountSummaryOpt.isPresent()) {
                conversationCountSummary = conversationCountSummaryOpt.get();
                conversationCountSummary.setStatus(EnumSummaryStatus.AGENT_TERMINATE);
                conversationCountSummaryRepository.save(conversationCountSummary);
            } else {
                LOGGER.error("Five9 terminate summary cannot be process because row doesn't exists");
            }
        } catch (Exception exception) {
            LOGGER.error("Error while executing process terminate five9 count summary {}", exception.getMessage());
        } finally {
            LOGGER.info("Process five9 count terminate summary successfully process");
        }
    }

}
