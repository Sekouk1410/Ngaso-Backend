package com.ngaso.Ngaso.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjetAdminItemResponse {
    private Integer id;
    private String titre;
    private String proprietaireNom;
    private String proprietairePrenom;
    private String localisation;
    private Double budget;
    private String currentEtape;
    private Integer progressPercent;
    private Date dateCreation;
}
