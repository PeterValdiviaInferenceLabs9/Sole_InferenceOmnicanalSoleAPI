package com.inference.whatsappintegration.infrastructure.adapter;

import com.inference.whatsappintegration.application.dto.inferenceoutrequest.InferenceOutRequest;
import com.inference.whatsappintegration.application.dto.inferenceoutresponse.InferenceOutResponse;
import com.inference.whatsappintegration.domain.service.InferenceBotApi;
import com.inference.whatsappintegration.util.Constants;
import com.inference.whatsappintegration.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InferenceBotApiServiceImp implements InferenceBotApi {

    private static final Logger LOGGER  = LoggerFactory.getLogger(InferenceBotApiServiceImp.class);

    @Value("${property.inference.bot.url}")
    private String inferenceUrl;

    public InferenceOutResponse sendMessageBot(InferenceOutRequest inferenceOutRequest) {
        RestTemplate restTemplate = new RestTemplate();

        Utils.logAsJson(LOGGER, inferenceOutRequest, "InferenceOutRequest");

        ResponseEntity<InferenceOutResponse> inferenceOutResponseResponseResult = restTemplate.postForEntity(
                inferenceUrl, inferenceOutRequest, InferenceOutResponse.class
        );
        if (Constants.SUCCESS_STATUS_CODES.contains(inferenceOutResponseResponseResult.getStatusCode().value())){
            LOGGER.info("Success in sending request to bot");
            Utils.logAsJson(LOGGER, inferenceOutResponseResponseResult.getBody(), "InferenceOutResponse");
            return inferenceOutResponseResponseResult.getBody();
        } else {
            LOGGER.error("Error while sending request to bot");
            return null;
        }
    }
}
