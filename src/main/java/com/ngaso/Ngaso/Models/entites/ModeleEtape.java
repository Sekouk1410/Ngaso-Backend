package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class ModeleEtape {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer ordre;

    @ManyToMany
    @JoinTable(
            name = "modele_etape_specialites",
            joinColumns = @JoinColumn(name = "modele_etape_id"),
            inverseJoinColumns = @JoinColumn(name = "specialite_id")
    )
    private Set<Specialite> specialites = new HashSet<>();

    @OneToMany(mappedBy = "modele", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Illustration> illustrations = new ArrayList<>();

}
