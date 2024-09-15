package com.inference.whatsappintegration.application.dto.whatsappinrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfo {
    private String userName;
    private String firstName;
    private String lastName;
    private String avatarUrl;
}
