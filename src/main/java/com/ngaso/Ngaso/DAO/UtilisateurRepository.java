package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {}
