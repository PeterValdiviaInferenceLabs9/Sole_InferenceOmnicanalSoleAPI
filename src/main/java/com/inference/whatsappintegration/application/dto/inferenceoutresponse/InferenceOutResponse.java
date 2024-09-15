package com.inference.whatsappintegration.application.dto.inferenceoutresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InferenceOutResponse {

    private int status;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("message")
    private String messageResponse;

    @JsonProperty("message_json")
    private MessageJson messageJson;
}
