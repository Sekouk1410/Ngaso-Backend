package com.ngaso.Ngaso.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthLoginRequest {
    private String email;     // utilisé pour admin
    private String telephone; // utilisé pour novice/professionnel
    private String password;
}
