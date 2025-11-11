package com.ngaso.Ngaso.dto;

import com.ngaso.Ngaso.Models.enums.StatutDevis;
import java.util.Date;

public class  PropositionDevisResponse {

    private Integer id;
    private Double montant;
    private String description;
    private Integer specialiteId;
    private String fichierDevis;
    private StatutDevis statut;
    private ProfessionnelProfilResponse professionnel;
    private String projetTitre;
    private Date dateProposition;
    private String noviceNom;
    private String novicePrenom;

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

    public ProfessionnelProfilResponse getProfessionnel() { return professionnel; }
    public void setProfessionnel(ProfessionnelProfilResponse professionnel) { this.professionnel = professionnel; }

    public String getProjetTitre() { return projetTitre; }
    public void setProjetTitre(String projetTitre) { this.projetTitre = projetTitre; }

    public Date getDateProposition() { return dateProposition; }
    public void setDateProposition(Date dateProposition) { this.dateProposition = dateProposition; }

    public String getNoviceNom() { return noviceNom; }
    public void setNoviceNom(String noviceNom) { this.noviceNom = noviceNom; }

    public String getNovicePrenom() { return novicePrenom; }
    public void setNovicePrenom(String novicePrenom) { this.novicePrenom = novicePrenom; }
}