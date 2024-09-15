package com.inference.whatsappintegration.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class SessionExpiredMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionExpiredMessageListener.class);
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        LOGGER.info("Session with key {} has expired", expiredKey);
    }
}
