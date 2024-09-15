package com.inference.whatsappintegration.domain.service;

import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;

public interface RedisService {
    void saveWithAudit(Sessions entity, Long expirationTime);
}
