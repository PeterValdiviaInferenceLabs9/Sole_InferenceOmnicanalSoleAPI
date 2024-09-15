package com.inference.whatsappintegration.application.dto.conversationsdto.five9acknowledgemessagerequest;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Five9AcknowledgeMessageRequest {

    private List<AcknowledgeMessage> messages;

}
