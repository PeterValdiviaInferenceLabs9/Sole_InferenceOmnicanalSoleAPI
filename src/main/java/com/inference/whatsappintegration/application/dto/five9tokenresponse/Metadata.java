package com.inference.whatsappintegration.application.dto.five9tokenresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
    private String freedomUrl;
    private ArrayList<DataCenter> dataCenters;
}
