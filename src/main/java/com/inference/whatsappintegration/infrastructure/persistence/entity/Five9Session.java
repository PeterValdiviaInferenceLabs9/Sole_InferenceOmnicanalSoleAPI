package com.inference.whatsappintegration.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import com.inference.whatsappintegration.domain.model.TransferAgentInformation;

@Data
@RedisHash("Five9Session")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Five9Session{

    @Id
    private String tokenId;

    private String farmId;

    private String host;

    private String identifier;

    private String agentName;

    private TransferAgentInformation transferAgentInformation;

}
