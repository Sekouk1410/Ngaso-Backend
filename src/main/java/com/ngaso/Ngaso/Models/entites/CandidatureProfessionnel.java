package com.ngaso.Ngaso.Models.entites;

import com.ngaso.Ngaso.Models.enums.StatutCandidature;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class CandidatureProfessionnel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCandidature;

    private String message;

    @Enumerated(EnumType.STRING)
    private StatutCandidature statut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "etape_id")
    private EtapeConstruction etape;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professionnel_id")
    private Professionnel professionnel;

    @OneToOne(mappedBy = "candidature", cascade = CascadeType.ALL)
    private PropositionDevis devis;
}
