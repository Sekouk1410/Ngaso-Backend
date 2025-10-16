package com.ngaso.Ngaso.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessionnelSignupRequest {
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;
    private String password;
    private String entreprise;
    private String description;
    private String document_justificatif;
    private java.util.List<Integer> specialiteIds;
}
