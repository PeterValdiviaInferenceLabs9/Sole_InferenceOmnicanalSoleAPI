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
public class DataCenter {

    private String name;
    private ArrayList<UiUrl> uiUrls;
    private ArrayList<ApiUrl> apiUrls;
    private ArrayList<LoginUrl> loginUrls;
    private boolean active;
}
