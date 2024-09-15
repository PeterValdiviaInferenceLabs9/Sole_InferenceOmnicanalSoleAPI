package com.inference.whatsappintegration.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inference.whatsappintegration.application.dto.whatsappinrequest.Location;
import com.inference.whatsappintegration.application.dto.whatsappinrequest.WhatsappIncomingMessageRequestDTO;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.*;
import java.util.regex.Pattern;

public class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Utils() {
        throw new IllegalStateException("This is a utility class");
    }

    public static String generateConversationId(){
        return UUID.randomUUID().toString();
    }

    public static String generateRandomUUID(){
        return UUID.randomUUID().toString();
    }

    public static void setMDCParameters(Sessions incomingSession){
        MDC.put(Constants.MDC_DEFAULT_CONVERSATIONID, incomingSession.getConversationId());
    }

    public static void clearMDCParameters(){
        MDC.clear();
    }

    public static String findMatchingContentType(String inputStr) {
        for (String contentType : Constants.WHATSAPP_CONTENT_TYPES) {
            if (inputStr.contains(contentType)) {
                return contentType;
            }
        }
        return null;
    }

    public static String extractUrlFromMessage(String message) {
        for (String contentType : Constants.WHATSAPP_CONTENT_TYPES) {
            if (message.startsWith(contentType)) {
                return message.replaceFirst(Pattern.quote(contentType), Constants.EMPTY_STRING).trim();
            }
        }
        throw new IllegalArgumentException("Unknown content type in message: " + message);
    }

    public static void logAsJson(Logger logger, Object obj, String prefixMessage) {
        try {
            String json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            logger.info("{}: {}", prefixMessage, json);
        } catch (JsonProcessingException e) {
            logger.error("Error convirtiendo objeto a JSON", e);
        }
    }

    public static String replaceSpecialCharacters(String inputString){
        return inputString.replace(Constants.DEFAULT_INFERENCE_BOT_LINE_SEPARATOR,
                Constants.DEFAULT_INFERENCE_BOT_REPLACE_LINE_SEPARATOR);
    }

    public static List<String> splitSpecialMessageLinks(String inputMessage){
        List<String> listSpecialMessageLinks = new ArrayList<>();
        if (inputMessage.contains(Constants.DEFAULT_MESSAGE_SEPARATOR_SEARCHER)) {
            listSpecialMessageLinks.addAll(Arrays.asList(inputMessage.split(Constants.DEFAULT_MESSAGE_SEPARATOR)));
        } else {
            listSpecialMessageLinks.add(inputMessage);
        }
        return listSpecialMessageLinks;
    }

    public static String processWhatsAppType(WhatsappIncomingMessageRequestDTO whatsappIncomingMessageRequestDTO){
        String messageText;
        switch (whatsappIncomingMessageRequestDTO.getMessageContent().getType()){
            case Constants.WHATSAPP_CONTENT_TYPE_LOCATION:
                Location tmpLocation = whatsappIncomingMessageRequestDTO.getMessageContent().getLocation();
                messageText = Constants.GOOGLE_MAPS_LOCATION_QUERY + tmpLocation.getLatitude() + Constants.COMMA + tmpLocation.getLongitude();
                break;
            case Constants.WHATSAPP_CONTENT_TYPE_AUDIO:
            case Constants.WHATSAPP_CONTENT_TYPE_VIDEO:
            case Constants.WHATSAPP_CONTENT_TYPE_DOCUMENT:
            case Constants.WHATSAPP_CONTENT_TYPE_IMAGE:
                messageText = whatsappIncomingMessageRequestDTO.getMessageContent().getAttachment().getUrl();
                break;
            case Constants.WHATSAPP_CONTENT_TYPE_BUTTON:
                messageText = whatsappIncomingMessageRequestDTO.getMessageContent().getPayload();
                break;
            default:
                messageText = whatsappIncomingMessageRequestDTO.getMessageContent().getText();
                break;
        }
        return messageText;
    }

    public static boolean isNotEmptyStringValidation(String stringMsg) {
        return stringMsg != null && !stringMsg.trim().isEmpty();
    }


}
