package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
public class Professionnel extends Utilisateur {
    private String description;
    @ElementCollection
    @CollectionTable(name = "professionnel_realisations", joinColumns = @JoinColumn(name = "professionnel_id"))
    @Column(name = "realisation")
    private List<String> realisations = new ArrayList<>();
    private Boolean estValider;
    private String documentJustificatif;
    private String entreprise;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCréation;

    @ManyToOne
    @JoinColumn(name = "specialite_id", nullable = false)
    private Specialite specialite;

    @OneToMany(mappedBy = "professionnel")
    private List<DemandeService> demandes = new ArrayList<>();

    @OneToMany(mappedBy = "professionnel")
    private List<PropositionDevis> propositions = new ArrayList<>();

    @OneToMany(mappedBy = "professionnel")
    private List<CandidatureProfessionnel> candidatures = new ArrayList<>();
}
