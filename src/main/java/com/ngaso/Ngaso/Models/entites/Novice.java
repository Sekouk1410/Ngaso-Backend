package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Novice extends Utilisateur {

    @OneToMany(mappedBy = "novice")
    private List<DemandeService> demandes = new ArrayList<>();

    @OneToMany(mappedBy = "proprietaire")
    private List<ProjetConstruction> projets = new ArrayList<>();

    @OneToMany(mappedBy = "novice")
    private List<PropositionDevis> propositions = new ArrayList<>();
}
