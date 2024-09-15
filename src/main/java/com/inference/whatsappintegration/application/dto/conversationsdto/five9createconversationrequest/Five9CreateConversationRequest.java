package com.inference.whatsappintegration.application.dto.conversationsdto.five9createconversationrequest;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Five9CreateConversationRequest {

    private String campaignName;
    private int tenantId;
    private String externalId;
    private String type;
    private Contact contact;
    private int priority;
    private String callbackUrl;
    private Attributes attributes;

}
