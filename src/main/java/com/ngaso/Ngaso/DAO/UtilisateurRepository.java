package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Utilisateur;
import java.util.Optional;
import com.ngaso.Ngaso.Models.enums.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByTelephone(String telephone);
    boolean existsByEmail(String email);

    long countByRole(Role role);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.dateInscription BETWEEN :start AND :end")
    long countRegisteredBetween(@Param("start") Date start, @Param("end") Date end);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role AND u.dateInscription BETWEEN :start AND :end")
    long countByRoleAndDateInscriptionBetween(@Param("role") Role role, @Param("start") Date start, @Param("end") Date end);
}
