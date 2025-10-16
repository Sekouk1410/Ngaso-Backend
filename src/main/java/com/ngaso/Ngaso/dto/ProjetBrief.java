package com.ngaso.Ngaso.dto;

import java.util.Date;

public class ProjetBrief {
    private Integer id;
    private String titre;
    private Date dateCreation;
    private String proprietaireNom;
    private String localisation;
    private Double budget;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public String getProprietaireNom() { return proprietaireNom; }
    public void setProprietaireNom(String proprietaireNom) { this.proprietaireNom = proprietaireNom; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
}
