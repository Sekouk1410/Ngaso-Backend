package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import com.ngaso.Ngaso.Models.enums.TypeNotification;

@Getter
@Setter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    private String contenu;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private Boolean estVu;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur destinataire;
}
