package com.ngaso.Ngaso.Models.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Getter
@Setter
@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Boolean etat;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToOne
    @JoinColumn(name = "proposition_id", unique = true)
    private PropositionDevis proposition;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
}
