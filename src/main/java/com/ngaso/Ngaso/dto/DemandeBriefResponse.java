package com.ngaso.Ngaso.dto;

import com.ngaso.Ngaso.Models.enums.StatutDemande;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandeBriefResponse {
    private Integer id;
    private String message;
    private StatutDemande statut;
    private Date dateCreation;
    private Integer professionnelId;
    private String professionnelNom;
    private String professionnelPrenom;
    private String professionnelEntreprise;
    private Double projetBudget;
}
