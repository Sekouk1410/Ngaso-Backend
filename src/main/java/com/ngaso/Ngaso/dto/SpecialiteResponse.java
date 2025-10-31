package com.ngaso.Ngaso.dto;

public class SpecialiteResponse {
    private Integer id;
    private String libelle;

    public SpecialiteResponse(Integer id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public Integer getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

}
