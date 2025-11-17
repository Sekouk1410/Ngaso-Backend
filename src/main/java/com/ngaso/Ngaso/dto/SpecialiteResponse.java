package com.ngaso.Ngaso.dto;

public class SpecialiteResponse {
    private Integer id;
    private String libelle;
    private Long nombreProfessionnels;

    public SpecialiteResponse(Integer id, String libelle, Long nombreProfessionnels) {
        this.id = id;
        this.libelle = libelle;
        this.nombreProfessionnels = nombreProfessionnels;
    }

    public Integer getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public Long getNombreProfessionnels() {
        return nombreProfessionnels;
    }

}
