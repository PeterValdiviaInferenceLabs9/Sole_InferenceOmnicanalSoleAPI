package com.inference.whatsappintegration.application.dto.whatappoutrequest;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class WhatsappMessage extends Message{

    private Content content;

}
