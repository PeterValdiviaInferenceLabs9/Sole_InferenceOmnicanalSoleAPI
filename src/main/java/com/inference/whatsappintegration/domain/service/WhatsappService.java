package com.inference.whatsappintegration.domain.service;

import com.inference.whatsappintegration.application.dto.whatsappinrequest.WhatsappIncomingMessageRequestDTO;

public interface WhatsappService {

    void processReceiveInteraction(WhatsappIncomingMessageRequestDTO whatsappIncomingMessageRequestDTO);

}
