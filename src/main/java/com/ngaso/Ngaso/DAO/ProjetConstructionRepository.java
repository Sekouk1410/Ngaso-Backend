package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.ngaso.Ngaso.Models.entites.ProjetConstruction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.ngaso.Ngaso.Models.enums.EtatProjet;

import java.util.Date;

public interface ProjetConstructionRepository extends JpaRepository<ProjetConstruction, Integer> {
    List<ProjetConstruction> findByProprietaire_Id(Integer proprietaireId);

    @Query("SELECT p FROM ProjetConstruction p WHERE p.demande.professionnel.id = :proId ORDER BY p.dateCréation DESC")
    List<ProjetConstruction> findLastByProfessionnel(@Param("proId") Integer professionnelId, Pageable pageable);

    @Query("SELECT p FROM ProjetConstruction p ORDER BY p.dateCréation DESC")
    List<ProjetConstruction> findLastGlobal(Pageable pageable);

    List<ProjetConstruction> findByEtat(EtatProjet etat);

    Page<ProjetConstruction> findByEtat(EtatProjet etat, Pageable pageable);

    @Query("SELECT p FROM ProjetConstruction p WHERE p.proprietaire.id = :noviceId ORDER BY p.dateCréation DESC")
    List<ProjetConstruction> findLastByNovice(@Param("noviceId") Integer noviceId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM ProjetConstruction p WHERE p.dateCréation BETWEEN :start AND :end")
    long countCreatedBetween(@Param("start") Date start, @Param("end") Date end);

    @Query("SELECT COUNT(p) FROM ProjetConstruction p WHERE p.etat = :etat")
    long countByEtat(@Param("etat") EtatProjet etat);

    @Query("SELECT COUNT(p) FROM ProjetConstruction p WHERE p.etat = :etat AND p.dateCréation BETWEEN :start AND :end")
    long countByEtatAndDateCreatedBetween(@Param("etat") EtatProjet etat, @Param("start") Date start, @Param("end") Date end);
}
