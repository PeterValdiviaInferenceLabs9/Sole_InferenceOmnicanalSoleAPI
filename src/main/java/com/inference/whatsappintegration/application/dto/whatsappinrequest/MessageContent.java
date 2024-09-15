package com.inference.whatsappintegration.application.dto.whatsappinrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageContent {
    private String text;
    private String type;
    private String caption;
    private String payload;
    private List<Item> items;
    private Location location;
    private Attachment attachment;
}
