package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Illustration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titre;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String urlImage;

    @ManyToOne
    @JoinColumn(name = "modele_id")
    private ModeleEtape modele;
}
