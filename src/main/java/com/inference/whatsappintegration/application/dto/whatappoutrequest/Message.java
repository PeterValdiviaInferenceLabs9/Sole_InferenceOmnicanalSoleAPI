package com.inference.whatsappintegration.application.dto.whatappoutrequest;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class Message {

    private String requestId;
    private String cascadeId;
    private SubscriberFilter subscriberFilter;
    private String startTime;

}
