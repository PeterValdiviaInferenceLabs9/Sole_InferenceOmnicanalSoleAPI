package com.inference.whatsappintegration.domain.service;

import com.inference.whatsappintegration.application.dto.inferenceoutrequest.InferenceOutRequest;
import com.inference.whatsappintegration.application.dto.inferenceoutresponse.InferenceOutResponse;

public interface InferenceBotApi {

    InferenceOutResponse sendMessageBot(InferenceOutRequest inferenceOutRequest);

}
