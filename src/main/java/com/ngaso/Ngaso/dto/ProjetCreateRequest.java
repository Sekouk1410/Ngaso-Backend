package com.ngaso.Ngaso.dto;
public class ProjetCreateRequest {
    private Integer noviceId;
    private String titre;
    private String dimensionsTerrain;
    private Double budget;
    private String localisation;

    public Integer getNoviceId() { return noviceId; }
    public void setNoviceId(Integer noviceId) { this.noviceId = noviceId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDimensionsTerrain() { return dimensionsTerrain; }
    public void setDimensionsTerrain(String dimensionsTerrain) { this.dimensionsTerrain = dimensionsTerrain; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
}
