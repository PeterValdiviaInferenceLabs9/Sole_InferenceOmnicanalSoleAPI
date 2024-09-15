package com.inference.whatsappintegration.domain.service;

import com.inference.whatsappintegration.application.dto.whatappoutrequest.WhatsappMessage;

public interface WhatsappMessageAPI {

    void sendMessageResponse(WhatsappMessage whatsappMessage);

}
