package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.ngaso.Ngaso.Models.entites.ProjetConstruction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import com.ngaso.Ngaso.Models.enums.EtatProjet;

public interface ProjetConstructionRepository extends JpaRepository<ProjetConstruction, Integer> {
    List<ProjetConstruction> findByProprietaire_Id(Integer proprietaireId);

    @Query("SELECT p FROM ProjetConstruction p WHERE p.demande.professionnel.id = :proId ORDER BY p.dateCréation DESC")
    List<ProjetConstruction> findLastByProfessionnel(@Param("proId") Integer professionnelId, Pageable pageable);

    @Query("SELECT p FROM ProjetConstruction p ORDER BY p.dateCréation DESC")
    List<ProjetConstruction> findLastGlobal(Pageable pageable);

    List<ProjetConstruction> findByEtat(EtatProjet etat);
}
