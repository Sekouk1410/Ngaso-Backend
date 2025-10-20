package com.ngaso.Ngaso.dto;

import com.ngaso.Ngaso.Models.enums.StatutDevis;

public class PropositionDevisResponse {
    private Integer id;
    private Double montant;
    private String description;
    private Integer specialiteId;
    private String fichierDevis;
    private StatutDevis statut;
    private ProfessionnelBriefResponse professionnel;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSpecialiteId() { return specialiteId; }
    public void setSpecialiteId(Integer specialiteId) { this.specialiteId = specialiteId; }

    public String getFichierDevis() { return fichierDevis; }
    public void setFichierDevis(String fichierDevis) { this.fichierDevis = fichierDevis; }

    public StatutDevis getStatut() { return statut; }
    public void setStatut(StatutDevis statut) { this.statut = statut; }

    public ProfessionnelBriefResponse getProfessionnel() { return professionnel; }
    public void setProfessionnel(ProfessionnelBriefResponse professionnel) { this.professionnel = professionnel; }
}
