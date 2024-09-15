package com.inference.whatsappintegration.application.dto.inferenceoutrequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Builder
@Getter
public class InferenceOutRequest {

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("task_key")
    private String taskKey;

    @JsonProperty("session_id")
    private String sessionId;

    private String text;

    @JsonProperty("user_key")
    private String userKey;

    @JsonProperty("message_json")
    private MessageJson messageJson;
}
