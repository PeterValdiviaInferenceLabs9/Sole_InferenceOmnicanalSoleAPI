package com.inference.whatsappintegration.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {

    private Constants() {
        throw new IllegalStateException("This is a utility class");
    }

    public static final String EMPTY_STRING = "";
    public static final String WHATSAPP_CHANNEL = "WHATSAPP";
    public static final String INFERENCE_BOT_RECEIVER = "Inference Bot";
    public static final String FIVE9_RECEIVER = "Five9";
    public static final String INBOUND_TYPE = "Inbound";
    public static final String OUTBOUND_TYPE = "Outbound";
    public static final Set<String> WHATSAPP_CONTENT_TYPES = new HashSet<>
            (Arrays.asList("[IMAGE]", "[DOCUMENT]", "[VIDEO]", "[AUDIO]"));

    public static final int CHANNEL_TYPE_INFERENCE_BOT = 0;
    public static final int CHANNEL_TYPE_FIVE9_AGENT = 1;
    public static final int CHANNEL_TYPE_SURVEY = 2;
    public static final int BOT_FLAG_DEACTIVATED = 1;

    public static final long EXPIRATION_TIME_SURVEY = 86400;
    public static final long EXPIRATION_TIME_DEFAULT = 3600;

    public static final String DEV_SOLE_TASK_KEY_MAIN_ATTENTION = "CHAT00000000c5";
    public static final String DEV_SOLE_TASK_KEY_SURVEY = "CHAT00000000cf";
    public static final String SOLE_TASK_KEY_MAIN_ATTENTION = "CHAT000000002f";
    public static final String SOLE_TASK_KEY_SURVEY = "CHAT0000000035";
    public static final String SOLE_TENANT_NAME = "SOLE";


    public static final Set<Integer> SUCCESS_STATUS_CODES = new HashSet<>(Arrays.asList(200, 201, 202, 204));

    public static final Set<String> SUBJECT_ID_NO_SURVEY = new HashSet<>(Arrays.asList("708"));

    public static final int CREDIT_EXPIRED_STATUS_CODE = 425;

    public static final String CASCADE_DUMMY = "301";
    public static final String CASCADE_SOLE_INMO= "733";

    public static final String SUBSCRIBE_FILTER_TYPE_PHONE = "PHONE";
    public static final String WHATSAPP_CONTENT_TYPE_TEXT = "TEXT";
    public static final String WHATSAPP_CONTENT_TYPE_VIDEO = "VIDEO";
    public static final String WHATSAPP_CONTENT_TYPE_AUDIO = "AUDIO";
    public static final String WHATSAPP_CONTENT_TYPE_DOCUMENT = "DOCUMENT";
    public static final String WHATSAPP_CONTENT_TYPE_LOCATION = "LOCATION";
    public static final String WHATSAPP_CONTENT_TYPE_IMAGE = "IMAGE";
    public static final String WHATSAPP_CONTENT_TYPE_BUTTON = "BUTTON";

    public static final String GOOGLE_MAPS_LOCATION_QUERY = "https://www.google.com/maps/search/?api=1&query=";

    public static final String BROKER_API_KEY_HEADER = "x-api-key";

    public static final String DEFAULT_FIVE9_API_HOST = "app-atl.five9.com";
    public static final int DEFAULT_FIVE9_PRIORITY= 1 ;
    public static final String DEFAULT_FIVE9_CONTEXT_PATH = "/five9";
    public static final String DEFAULT_FIVE9_MESSAGES_PATH = "/messages";
    public static final String DEFAULT_FIVE9_WHATSAPP_IMAGE_PROFILE="https://fg.ull.es/empleo/wp-content/uploads/sites/6/2020/09/whatsapp_png.png";

    public static final String DEFAULT_FIVE9_ACKNOWLEDGE_PATH = "/acknowledge";
    public static final String FIVE9_FARMID_HEADER = "farmId";

    public static final String HTTPS_PREFIX = "https://";
    public static final String SLASH = "/";
    public static final String COMMA = ",";

    public static final String MDC_DEFAULT_CONVERSATIONID = "conversationId";
    public static final String DEFAULT_MESSAGE_TERMINATION = "Muchas gracias por comunicarse con SOLE.";

    public static final String DEFAULT_INFERENCE_BOT_LINE_SEPARATOR = "\\n";
    public static final String DEFAULT_INFERENCE_BOT_REPLACE_LINE_SEPARATOR = "\n";
    public static final String DEFAULT_MESSAGE_SEPARATOR_SEARCHER = "[LINK]";
    public static final String DEFAULT_MESSAGE_SEPARATOR = "\\[LINK\\]";

    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int DEFAULT_SUMMARY_COUNT_INITIALIZER = 1;
    public static final int DEFAULT_SUMMARY_TOTAL_COUNT_INITIALIZER = 2;
    public static final int DEFAULT_FIVE9_SUMMARY_COUNT_INITIALIZER = 0;

    public static final String DEFAULT_WSP_DELIVERY_STATUS_SENT = "SENT";

}
