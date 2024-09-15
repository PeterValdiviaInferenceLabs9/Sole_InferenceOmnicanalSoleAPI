package com.inference.whatsappintegration.application.dto.inferenceoutrequest;

import lombok.*;


@Getter
@Builder
public class MessageJson {

    private String userId;

    private String idRecipient;

    private String channel;

    private String clientName;

    private String agent;
}
