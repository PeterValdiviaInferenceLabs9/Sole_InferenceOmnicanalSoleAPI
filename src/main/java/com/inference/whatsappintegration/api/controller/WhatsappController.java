package com.inference.whatsappintegration.api.controller;

import com.inference.whatsappintegration.application.dto.whatsappdeliverystatus.WhatsappDeliveryStatus;
import com.inference.whatsappintegration.application.dto.whatsappinrequest.WhatsappIncomingMessageRequestDTO;
import com.inference.whatsappintegration.domain.service.WhatsappService;
import com.inference.whatsappintegration.infrastructure.config.mdc.MdcAwareExecutor;
import com.inference.whatsappintegration.util.Constants;
import com.inference.whatsappintegration.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/whatsapp")
public class WhatsappController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhatsappController.class);

    private WhatsappService processWhatsappInteraction;
    private MdcAwareExecutor mdcAwareExecutor;

    public WhatsappController(WhatsappService processWhatsappInteraction, MdcAwareExecutor mdcAwareExecutor) {
        this.processWhatsappInteraction = processWhatsappInteraction;
        this.mdcAwareExecutor = mdcAwareExecutor;
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> getCallback() {
        LOGGER.info("Get callback call");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/callback")
    public ResponseEntity<String> eventInput(@RequestBody WhatsappIncomingMessageRequestDTO whatsappIncomingMessageRequestDTO) {
        LOGGER.info("Receive whatsapp process request");
        mdcAwareExecutor.execute(() -> processWhatsappInteraction.processReceiveInteraction(whatsappIncomingMessageRequestDTO));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<Void> getStatus() {
        LOGGER.info("Get status call");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/status")
    public ResponseEntity<String> eventStatus(@RequestBody WhatsappDeliveryStatus whatsappDeliveryStatus) {
        if (whatsappDeliveryStatus.getStatus().equals(Constants.DEFAULT_WSP_DELIVERY_STATUS_SENT)) {
            LOGGER.info("Receive whatsapp sent status");
            Utils.logAsJson(LOGGER, whatsappDeliveryStatus, "WhatsappDeliveryStatusRequest");
        }
        return ResponseEntity.ok().build();
    }

}
