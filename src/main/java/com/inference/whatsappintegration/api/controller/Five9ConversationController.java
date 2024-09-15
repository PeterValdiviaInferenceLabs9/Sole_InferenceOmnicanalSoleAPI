package com.inference.whatsappintegration.api.controller;

import com.inference.whatsappintegration.application.dto.conversationseventsdto.conversationmessageeventrequest.ConversationMessageEventRequest;
import com.inference.whatsappintegration.application.dto.conversationseventsdto.conversationterminateevent.ConversationTerminateEvenRequest;
import com.inference.whatsappintegration.domain.service.Five9ConversationService;
import com.inference.whatsappintegration.infrastructure.config.mdc.MdcAwareExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/five9/conversations/{conversationId}")
public class Five9ConversationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Five9ConversationController.class);

    private Five9ConversationService five9ConversationService;
    private MdcAwareExecutor mdcAwareExecutor;

    public Five9ConversationController(Five9ConversationService five9ConversationService
            , MdcAwareExecutor mdcAwareExecutor) {
        this.five9ConversationService = five9ConversationService;
        this.mdcAwareExecutor = mdcAwareExecutor;
    }

    @PostMapping("/message")
    public ResponseEntity<String> messageAddedToConversation(@PathVariable(value="conversationId") String conversationId,
                                                             @RequestBody ConversationMessageEventRequest conversationMessageEventRequest){
        LOGGER.info("Receive new message to conversation request");
        mdcAwareExecutor.execute(() -> five9ConversationService.processFive9MessageAdded(conversationId, conversationMessageEventRequest));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/terminate")
    public ResponseEntity<String> terminateConversation(@PathVariable (value="conversationId") String conversationId,
                                                        @RequestBody ConversationTerminateEvenRequest conversationTerminateEvenRequest){
        LOGGER.info("Receive terminate conversation request");
        mdcAwareExecutor.execute(() -> five9ConversationService.processFive9Terminate(conversationId, conversationTerminateEvenRequest));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/typing")
    public ResponseEntity<String> agentTyping(@PathVariable (value="conversationId") String conversationId){
        LOGGER.info("Receive typing conversation request");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<String> conversationCreated(@PathVariable (value="conversationId") String conversationId){
        LOGGER.info("Receive created conversation request");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/accept")
    public ResponseEntity<String> agentAcceptConversation(@PathVariable (value="conversationId") String conversationId){
        LOGGER.info("Receive accept conversation request");
        return ResponseEntity.ok().build();
    }
}
