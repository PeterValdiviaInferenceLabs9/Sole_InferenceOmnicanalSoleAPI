package com.inference.whatsappintegration.infrastructure.persistence.repository;

import com.inference.whatsappintegration.infrastructure.persistence.entity.ConversationCountSummary;
import com.inference.whatsappintegration.util.enums.EnumSummaryStatus;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface ConversationCountSummaryRepository extends CrudRepository<ConversationCountSummary, Long> {
    Optional<ConversationCountSummary> findTopByConversationIdAndStatusOrderByLastUpdatedAtDesc(String conversationId, EnumSummaryStatus status);

}
