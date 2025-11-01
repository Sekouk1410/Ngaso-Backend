package com.ngaso.Ngaso.Models.entites;

import com.ngaso.Ngaso.Models.enums.StatutDevis;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Entity
public class PropositionDevis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double montant;
    private String description;
    private String fichierDevis;

    @Enumerated(EnumType.STRING)
    private StatutDevis statut;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateProposition;

    @ManyToOne
    @JoinColumn(name = "professionnel_id")
    private Professionnel professionnel;

    @ManyToOne
    @JoinColumn(name = "novice_id")
    private Novice novice;

    @ManyToOne
    @JoinColumn(name = "demande_id")
    private DemandeService demande;

    @ManyToOne
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    @OneToOne
    @JoinColumn(name = "candidature_id", unique = true)
    private CandidatureProfessionnel candidature;
}
