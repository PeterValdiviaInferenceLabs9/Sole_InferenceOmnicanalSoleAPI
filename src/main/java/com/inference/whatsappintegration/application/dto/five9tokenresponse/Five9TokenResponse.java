package com.inference.whatsappintegration.application.dto.five9tokenresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Five9TokenResponse {

    private String tokenId;
    private String sessionId;
    private String orgId;
    private String userId;
    private Context context;
    private Metadata metadata;

}
