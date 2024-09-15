package com.inference.whatsappintegration.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@RedisHash("Session")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Sessions implements Serializable {

    @Id
    private String identifier;
    private String sessionId;
    private int channelType;
    private String conversationId;
    private String agentName;
    private String subjectId;
    @TimeToLive
    private Long expiration;

}
