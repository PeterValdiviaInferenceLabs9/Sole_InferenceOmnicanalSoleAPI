package com.inference.whatsappintegration.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TransferAgentInformation {

    private String email;

    private String clientRequest;

    private String campaign;

    private String agent;

    private String agentSkill;

}
