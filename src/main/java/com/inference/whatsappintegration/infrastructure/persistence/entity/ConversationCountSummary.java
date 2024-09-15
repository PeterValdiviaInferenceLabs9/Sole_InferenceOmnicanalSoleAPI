package com.inference.whatsappintegration.infrastructure.persistence.entity;

import com.inference.whatsappintegration.util.enums.EnumSummaryStatus;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONVERSATION_SUMMARY")
public class ConversationCountSummary extends BaseAudit{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String conversationId;
    private String idClient;
    private String agent;
    private String channel;
    private long countClient;
    private long countAgent;
    private long countTotal;
    private EnumSummaryStatus status;

}
