package com.inference.whatsappintegration.infrastructure.persistence.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "WSP_DAILY_CONVERSATION_HISTORY")
public class WhatsappDailyConversationHistory extends BaseAudit{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String conversationId;
    private String sender;
    private String receiver;
    private String type;
    @Column(columnDefinition="text")
    private String content;

}
