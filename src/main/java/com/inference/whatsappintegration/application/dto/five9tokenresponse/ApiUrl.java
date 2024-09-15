package com.inference.whatsappintegration.application.dto.five9tokenresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiUrl {

    private String host;
    private String port;
    private String routeKey;
    private String version;

}
