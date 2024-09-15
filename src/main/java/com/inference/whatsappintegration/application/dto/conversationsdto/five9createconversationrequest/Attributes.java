package com.inference.whatsappintegration.application.dto.conversationsdto.five9createconversationrequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Attributes {

    @JsonProperty("Question")
    private String question;

    @JsonProperty("agente_skill")
    private String agentSkill;

    @JsonProperty("agente_hsm")
    private String agentHSM;
}
