package com.inference.whatsappintegration.infrastructure.adapter;

import com.inference.whatsappintegration.application.dto.whatappoutrequest.WhatsappMessage;
import com.inference.whatsappintegration.application.dto.whatsappoutresponse.WhatsappOutResponse;
import com.inference.whatsappintegration.domain.service.WhatsappMessageAPI;
import com.inference.whatsappintegration.util.Constants;
import com.inference.whatsappintegration.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WhatsAppMessageAPIImp implements WhatsappMessageAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhatsAppMessageAPIImp.class);

    @Value("${property.whatsapp.broker.url}")
    private String whatsappBrokerEndpoint;

    @Value("${property.whatsapp.broker.api.key}")
    private String brokerApiKey;

    public void sendMessageResponse(WhatsappMessage whatsappMessage) {
        LOGGER.info("Starting sending whatsapp response");
        try{
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(Constants.BROKER_API_KEY_HEADER, brokerApiKey);

            Utils.logAsJson(LOGGER, whatsappMessage, "WhatsappMessageRequest");

            HttpEntity<WhatsappMessage> entity = new HttpEntity<>(whatsappMessage, headers);

            ResponseEntity<WhatsappOutResponse> whatsappOutResponseResponse = restTemplate.postForEntity(
                    whatsappBrokerEndpoint, entity, WhatsappOutResponse.class
            );
            if (Constants.SUCCESS_STATUS_CODES.contains(whatsappOutResponseResponse.getStatusCode().value())){
                Utils.logAsJson(LOGGER, whatsappOutResponseResponse.getBody(), "WhatsappMessageResponse");
                LOGGER.info("Success in sending whatsapp message");
            } else {
                LOGGER.error("Error while sending whatsapp message");
            }
        } catch (Exception ex) {
            LOGGER.error("Exception while sending whatsapp message response: {}", ex.getMessage());
        }  finally {
            LOGGER.info("Finish sending whatsapp response");
        }
    }
}