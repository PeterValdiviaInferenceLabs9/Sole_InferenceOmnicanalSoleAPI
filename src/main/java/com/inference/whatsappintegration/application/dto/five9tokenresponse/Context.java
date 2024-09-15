package com.inference.whatsappintegration.application.dto.five9tokenresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Context {
    private String cloudClientUrl;
    private String cloudTokenUrl;
    private String farmId;

}
