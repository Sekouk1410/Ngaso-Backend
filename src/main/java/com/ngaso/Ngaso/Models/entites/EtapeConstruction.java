package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class EtapeConstruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEtape;

    private Boolean estValider;

    @ManyToOne
    @JoinColumn(name = "projet_id")
    private ProjetConstruction projet;

    @ManyToOne
    @JoinColumn(name = "modele_id")
    private ModeleEtape modele;

    @OneToMany(mappedBy = "etape", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidatureProfessionnel> candidatures = new ArrayList<>();
}
