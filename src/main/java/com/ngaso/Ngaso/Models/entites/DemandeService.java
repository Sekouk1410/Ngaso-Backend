package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import com.ngaso.Ngaso.Models.enums.StatutDemande;

@Getter
@Setter
@Entity
public class DemandeService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String message;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCréation;

    @ManyToOne
    @JoinColumn(name = "novice_id")
    private Novice novice;

    @ManyToOne
    @JoinColumn(name = "professionnel_id")
    private Professionnel professionnel;

    @ManyToOne
    @JoinColumn(name = "etape_id")
    private EtapeConstruction etape;

    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjetConstruction> projets = new ArrayList<>();

    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropositionDevis> propositions = new ArrayList<>();
}
