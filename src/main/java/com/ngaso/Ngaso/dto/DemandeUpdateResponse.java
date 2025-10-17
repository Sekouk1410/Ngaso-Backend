package com.ngaso.Ngaso.dto;

import com.ngaso.Ngaso.Models.enums.StatutDemande;

public class DemandeUpdateResponse {
    private Integer id;
    private StatutDemande statut;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }
}
