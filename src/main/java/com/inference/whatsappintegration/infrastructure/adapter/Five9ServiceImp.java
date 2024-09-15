package com.inference.whatsappintegration.infrastructure.adapter;

import com.inference.whatsappintegration.application.dto.conversationsdto.five9acknowledgemessagerequest.Five9AcknowledgeMessageRequest;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9conversationsendmessagerequest.SendConversationMessageRequest;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9createconversationrequest.Five9CreateConversationRequest;
import com.inference.whatsappintegration.application.dto.conversationsdto.five9createconversationresponse.Five9CreateConversationResponse;
import com.inference.whatsappintegration.application.dto.five9tokenrequest.Five9TokenRequest;
import com.inference.whatsappintegration.application.dto.five9tokenresponse.Five9TokenResponse;
import com.inference.whatsappintegration.domain.service.Five9Service;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Five9Session;
import com.inference.whatsappintegration.util.Constants;
import com.inference.whatsappintegration.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Five9ServiceImp implements Five9Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(Five9ServiceImp.class);

    @Value("${property.five9.token.url}")
    private String five9EndpointDefault;
    @Value("${property.five9.default.path.conversation}")
    private String five9ConversationContextPath;

    public Five9TokenResponse getToken(Five9TokenRequest five9TokenRequest) {
        LOGGER.info("Starting processing five9 get token");
        Five9TokenResponse five9TokenResponse = null;
        try {
            RestTemplate restTemplate = new RestTemplate();

            Utils.logAsJson(LOGGER, five9TokenRequest, "Five9TokenRequest");

            ResponseEntity<Five9TokenResponse> five9TokenResponseResult = restTemplate.postForEntity(
                    five9EndpointDefault, five9TokenRequest, Five9TokenResponse.class
            );

            if (Constants.SUCCESS_STATUS_CODES.contains(five9TokenResponseResult.getStatusCode().value())){
                LOGGER.info("Success to get five 9 token");
                Utils.logAsJson(LOGGER, five9TokenResponseResult.getBody(), "Five9TokenResponse");
                five9TokenResponse = five9TokenResponseResult.getBody();
            } else {
                LOGGER.error("Cannot retrieve token");
            }
        } catch(Exception ex){
            LOGGER.warn("Exception while getting five9 token: {}", ex.getMessage());
        } finally{
            LOGGER.info("Finish processing five9 get token");
        }
        return five9TokenResponse;

    }


    public Five9CreateConversationResponse createConversation(Five9CreateConversationRequest five9CreateConversationRequest
            , Five9Session five9Session) {
        LOGGER.info("Starting processing five9 create conversation");
        Five9CreateConversationResponse five9CreateConversationResponse = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(five9Session.getTokenId());
            headers.set(Constants.FIVE9_FARMID_HEADER, five9Session.getFarmId());

            Utils.logAsJson(LOGGER, five9CreateConversationRequest, "Five9CreateConversationRequest");


            HttpEntity<Five9CreateConversationRequest> entity = new HttpEntity<>(five9CreateConversationRequest, headers);
            ResponseEntity<Five9CreateConversationResponse> five9CreateConversationResponseEntity = restTemplate.postForEntity(
                    Constants.HTTPS_PREFIX + five9Session.getHost() + five9ConversationContextPath, entity,
                    Five9CreateConversationResponse.class);

            if (Constants.SUCCESS_STATUS_CODES.contains(five9CreateConversationResponseEntity.getStatusCode().value())){
                LOGGER.info("Success to create conversation");
                Utils.logAsJson(LOGGER, five9CreateConversationResponseEntity.getBody(), "Five9CreateConversationResponse");
                five9CreateConversationResponse = five9CreateConversationResponseEntity.getBody();
            } else {
                LOGGER.error("Error while creating conversation!");
            }
        } catch(Exception ex){
            LOGGER.warn("Exception while creating five9 conversation: {}", ex.getMessage());
        } finally{
            LOGGER.info("Finish processing five9 create conversation");
        }
        return five9CreateConversationResponse;
    }

    public int sendFive9ConversationMessage(Five9Session five9Session
            , SendConversationMessageRequest sendConversationMessageRequest){
        LOGGER.info("Starting processing send message to five 9 conversation");
        int successTermination = 0;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(five9Session.getTokenId());
            headers.set(Constants.FIVE9_FARMID_HEADER, five9Session.getFarmId());

            Utils.logAsJson(LOGGER, sendConversationMessageRequest, "SendConversationMessageRequest");

            HttpEntity<SendConversationMessageRequest> entity = new HttpEntity<>(sendConversationMessageRequest, headers);
            ResponseEntity<Void> sendConversationMessageRequestResponse = restTemplate.postForEntity(
                    Constants.HTTPS_PREFIX + five9Session.getHost() + five9ConversationContextPath +
                            Constants.SLASH + five9Session.getTokenId() + Constants.DEFAULT_FIVE9_MESSAGES_PATH, entity,
                    Void.class);

            if (Constants.SUCCESS_STATUS_CODES.contains(sendConversationMessageRequestResponse.getStatusCode().value())) {
                Utils.logAsJson(LOGGER, sendConversationMessageRequestResponse.getBody()
                        , "SendConversationMessageResponse");
                LOGGER.info("Success in sending message to five 9");
            } else {
                successTermination = 1;
                LOGGER.error("Error while making the request to send conversation to five 9");
            }
        } catch(Exception ex){
                LOGGER.warn("Exception while trying to send message to five 9: {}", ex.getMessage());
                successTermination = 1;
        } finally{
            LOGGER.info("Finish processing send message to five9");
        }
        return successTermination;

    }

    public void sendAcknowledgeMessageToConversation(Five9Session five9Session,
                                                     Five9AcknowledgeMessageRequest five9AcknowledgeMessageRequest){
        LOGGER.info("Starting processing acknowledge message to five 9");
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(five9Session.getTokenId());
            headers.set(Constants.FIVE9_FARMID_HEADER, five9Session.getFarmId());

            String url = Constants.HTTPS_PREFIX + five9Session.getHost() + five9ConversationContextPath +
                    Constants.SLASH + five9Session.getTokenId() + Constants.DEFAULT_FIVE9_MESSAGES_PATH
                    + Constants.DEFAULT_FIVE9_ACKNOWLEDGE_PATH;

            HttpEntity<Five9AcknowledgeMessageRequest> entity = new HttpEntity<>(five9AcknowledgeMessageRequest, headers);
            ResponseEntity<Void> sendConversationMessageRequestResponse = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    Void.class);

            if (Constants.SUCCESS_STATUS_CODES.contains(sendConversationMessageRequestResponse.getStatusCode().value())){
                LOGGER.info("Success in acknowledge message to five 9");
            } else {
                LOGGER.error("Error while making the acknowledge message to five 9");
            }
        } catch (Exception ex) {
            LOGGER.warn("Warning while trying to message acknowledge event: {}", ex.getMessage());
        } finally {
            LOGGER.info("Finish processing acknowledge added event");
        }
    }
}
