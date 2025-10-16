package com.ngaso.Ngaso.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionnelSummaryResponse {
    private Integer id;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;
    private String entreprise;
    private String description;
    private Boolean estValider;
    private String documentJustificatif;
    private Integer specialiteId;
    private String specialiteLibelle;
}
