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
public class DemandeProItemResponse {
    private Integer id;
    private String message;
    private StatutDemande statut;
    private Date dateCreation;

    // Novice info
    private Integer noviceId;
    private String noviceNom;
    private String novicePrenom;
    private String noviceTelephone;

    // Étape / Projet info
    private Integer etapeId;
    private Integer projetId;
    private String projetTitre;
    private Integer etapeOrdre;
    private String etapeModeleNom;
}
