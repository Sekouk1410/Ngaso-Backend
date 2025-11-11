package com.ngaso.Ngaso.dto;

import java.util.List;

public class ProfessionnelProfilResponse {
    private Integer id;
    private String nom;
    private String prenom;
    private Integer specialiteId;
    private String specialiteLibelle;
    private String description;
    private List<String> realisations;
    private String telephone;
    private String email;
    private String adresse;
    private String entreprise;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Integer getSpecialiteId() { return specialiteId; }
    public void setSpecialiteId(Integer specialiteId) { this.specialiteId = specialiteId; }

    public String getSpecialiteLibelle() { return specialiteLibelle; }
    public void setSpecialiteLibelle(String specialiteLibelle) { this.specialiteLibelle = specialiteLibelle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRealisations() { return realisations; }
    public void setRealisations(List<String> realisations) { this.realisations = realisations; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getEntreprise() { return entreprise; }
    public void setEntreprise(String entreprise) { this.entreprise = entreprise; }
}
