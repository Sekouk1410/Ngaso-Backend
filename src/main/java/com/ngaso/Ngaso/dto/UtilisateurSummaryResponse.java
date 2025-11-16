package com.ngaso.Ngaso.dto;

import com.ngaso.Ngaso.Models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurSummaryResponse {
    private Integer id;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;
    private Role role;
    private Boolean actif;
    private Date dateInscription;
}
