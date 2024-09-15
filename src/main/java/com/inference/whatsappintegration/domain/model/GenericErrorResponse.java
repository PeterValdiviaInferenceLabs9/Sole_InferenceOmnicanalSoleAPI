package com.inference.whatsappintegration.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GenericErrorResponse {

    private int status;

    private String message;

    private long timeStamp;
}
