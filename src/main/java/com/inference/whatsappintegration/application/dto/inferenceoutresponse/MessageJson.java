package com.inference.whatsappintegration.application.dto.inferenceoutresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inference.whatsappintegration.util.enums.EnumBotStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageJson {

    @JsonProperty("correo")
    private String email;

    @JsonProperty("consulta")
    private String clientRequest;

    @JsonProperty("campania")
    private String campaign;

    @JsonProperty("agent")
    private String agent;

    @JsonProperty("agente_skill")
    private String agentSkill;

    @JsonProperty("botStatus")
    private EnumBotStatus botStatus;
}
