package com.ngaso.Ngaso.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoviceSignupRequest {
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;
    private String password;
}
