package com.inference.whatsappintegration.application.service.imp;

import com.inference.whatsappintegration.domain.service.RedisService;
import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;
import com.inference.whatsappintegration.infrastructure.persistence.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImp implements RedisService {

    private SessionRepository sessionRepository;


    public RedisServiceImp(SessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
    }

    public void saveWithAudit(Sessions entity, Long expirationTime) {
        entity.setExpiration(expirationTime);
        sessionRepository.save(entity);
    }
}
