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
    List<DemandeService> findByEtape_IdEtapeAndNovice_IdAndStatutNot(Integer etapeId, Integer noviceId, StatutDemande statut);
    List<DemandeService> findByEtape_Projet_IdProjetAndNovice_IdAndStatutNot(Integer projetId, Integer noviceId, StatutDemande statut);
    boolean existsByEtape_IdEtapeAndNovice_IdAndProfessionnel_IdAndStatut(Integer etapeId, Integer noviceId, Integer professionnelId, StatutDemande statut);
    List<DemandeService> findByProfessionnel_Id(Integer professionnelId);
    List<DemandeService> findByProfessionnel_IdAndStatut(Integer professionnelId, StatutDemande statut);
    long countByProfessionnel_IdAndEtapeIsNotNull(Integer professionnelId);
    long countByProfessionnel_IdAndEtapeIsNotNullAndStatut(Integer professionnelId, StatutDemande statut);
    List<DemandeService> findByProfessionnel_IdAndEtapeIsNotNull(Integer professionnelId);
    List<DemandeService> findByProfessionnel_IdAndEtapeIsNotNullAndStatut(Integer professionnelId, StatutDemande statut);
    List<DemandeService> findByProfessionnel_IdAndEtapeIsNotNullAndStatutNot(Integer professionnelId, StatutDemande statut);
}
