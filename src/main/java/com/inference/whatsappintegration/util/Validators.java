package com.inference.whatsappintegration.util;

import com.inference.whatsappintegration.application.dto.whatsappinrequest.WhatsappIncomingMessageRequestDTO;
import com.inference.whatsappintegration.infrastructure.exception.GenericException;
import com.inference.whatsappintegration.util.enums.EnumFieldWhatsappDTOMapping;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Validators {

    private Validators() {
        throw new IllegalStateException("This is a validator class");
    }

    public static void validateWhatsappDto(WhatsappIncomingMessageRequestDTO whatsappIncomingMessageRequestDTO){
        Map<String, String> fieldErrorMap = new LinkedHashMap<>();
        for (EnumFieldWhatsappDTOMapping mapping : EnumFieldWhatsappDTOMapping.values()) {
            fieldErrorMap.put(mapping.getFieldName(), mapping.getErrorMessage());
        }

        for (Map.Entry<String, String> entry : fieldErrorMap.entrySet()) {
            String fieldName = entry.getKey();
            PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(WhatsappIncomingMessageRequestDTO.class, fieldName);
            if (descriptor == null) {
                throw new GenericException("Please add field: " + fieldName);
            }
            try {
                Object fieldValue = descriptor.getReadMethod().invoke(whatsappIncomingMessageRequestDTO);
                if (fieldValue == null) {
                    throw new GenericException(entry.getValue());
                }
                if (whatsappIncomingMessageRequestDTO.getMessageContent().getType().equals(Constants.WHATSAPP_CONTENT_TYPE_TEXT)
                        && whatsappIncomingMessageRequestDTO.getMessageContent().getText().isEmpty()){
                    throw new GenericException("Text cannot be in blank!");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GenericException("Error while validating request DTO", e);
            }

        }
    }
}
