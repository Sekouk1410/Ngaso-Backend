package com.ngaso.Ngaso.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessionnelSignupRequest {
    private String nom;
    private String email;
    private String password;
    private String entreprise;
    private String description;
    private String document_justificatif;
}
