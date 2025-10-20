package com.ngaso.Ngaso.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionnelBriefResponse {
    private Integer id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String entreprise;
    private Integer specialiteId;
    private String specialiteLibelle;
    private List<String> realisations;
}
