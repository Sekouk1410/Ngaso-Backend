package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Specialite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String libelle;

    @OneToMany(mappedBy = "specialite")
    private Set<Professionnel> professionnels = new HashSet<>();
}
