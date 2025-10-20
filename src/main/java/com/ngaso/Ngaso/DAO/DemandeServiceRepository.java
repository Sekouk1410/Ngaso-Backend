package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.DemandeService;
import com.ngaso.Ngaso.Models.enums.StatutDemande;
import java.util.List;

public interface DemandeServiceRepository extends JpaRepository<DemandeService, Integer> {
    long countByProfessionnelId(Integer professionnelId);
    long countByProfessionnelIdAndStatut(Integer professionnelId, StatutDemande statut);
    List<DemandeService> findByEtape_IdEtapeAndNovice_Id(Integer etapeId, Integer noviceId);
    List<DemandeService> findByEtape_Projet_IdProjetAndNovice_Id(Integer projetId, Integer noviceId);
    boolean existsByEtape_IdEtapeAndNovice_IdAndProfessionnel_IdAndStatut(Integer etapeId, Integer noviceId, Integer professionnelId, StatutDemande statut);
    List<DemandeService> findByProfessionnel_Id(Integer professionnelId);
    List<DemandeService> findByProfessionnel_IdAndStatut(Integer professionnelId, StatutDemande statut);
}
