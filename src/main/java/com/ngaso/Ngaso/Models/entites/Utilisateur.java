package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import com.ngaso.Ngaso.Models.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false, unique = true)
    private String telephone;
    @Column(nullable = false)
    private String adresse;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    // Chemin de la photo de profil (stockée côté serveur)
    private String photoProfil;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "administrateur_id")
    private Administrateur administrateur;

    @ManyToMany
    @JoinTable(
            name = "utilisateur_conversation",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "conversation_id")
    )
    private Set<Conversation> conversations = new HashSet<>();

    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messagesEnvoyes = new ArrayList<>();

    @OneToMany(mappedBy = "destinataire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();
}

