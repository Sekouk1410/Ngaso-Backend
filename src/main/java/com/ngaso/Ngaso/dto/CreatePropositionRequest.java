package com.ngaso.Ngaso.dto;

public class CreatePropositionRequest {
    private Double montant;
    private String description;
    private Integer specialiteId; // optional

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSpecialiteId() { return specialiteId; }
    public void setSpecialiteId(Integer specialiteId) { this.specialiteId = specialiteId; }
}
