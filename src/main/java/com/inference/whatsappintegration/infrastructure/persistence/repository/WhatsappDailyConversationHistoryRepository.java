package com.inference.whatsappintegration.infrastructure.persistence.repository;

import com.inference.whatsappintegration.infrastructure.persistence.entity.WhatsappDailyConversationHistory;
import org.springframework.data.repository.CrudRepository;

public interface WhatsappDailyConversationHistoryRepository extends CrudRepository<WhatsappDailyConversationHistory, Long> {
}
