package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ngaso.Ngaso.Models.enums.EtatProjet;

@Getter
@Setter
@Entity
public class ProjetConstruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProjet;

    private String titre;
    private Double budget;
    private String localisation;
    private String dimensionsTerrain;

    @Enumerated(EnumType.STRING)
    private EtatProjet etat;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCréation;

    @ManyToOne
    @JoinColumn(name = "proprietaire_novice_id")
    private Novice proprietaire;

    @ManyToOne
    @JoinColumn(name = "demande_id")
    private DemandeService demande;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EtapeConstruction> etapes = new ArrayList<>();
}
