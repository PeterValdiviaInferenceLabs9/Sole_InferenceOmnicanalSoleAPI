package com.inference.whatsappintegration.application.dto.whatsappdeliverystatus;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WhatsappDeliveryStatus {

    private String requestId;

    private long cascadeId;

    private String cascadeStageUUID;

    private String subject;

    private String subjectId;

    private String status;

    private String statusAt;

    private String comment;

    private String error;

}
