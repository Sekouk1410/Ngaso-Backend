package com.ngaso.Ngaso.dto;

import com.ngaso.Ngaso.Models.enums.EtatProjet;
import java.util.Date;

public class ProjetResponse {
    private Integer id;
    private String titre;
    private Double budget;
    private String localisation;
    private String dimensionsTerrain;
    private EtatProjet etat;
    private Date dateCreation;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getDimensionsTerrain() { return dimensionsTerrain; }
    public void setDimensionsTerrain(String dimensionsTerrain) { this.dimensionsTerrain = dimensionsTerrain; }

    public EtatProjet getEtat() { return etat; }
    public void setEtat(EtatProjet etat) { this.etat = etat; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
}
