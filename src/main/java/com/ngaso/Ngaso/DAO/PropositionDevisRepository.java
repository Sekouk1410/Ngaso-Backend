package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.PropositionDevis;
import com.ngaso.Ngaso.Models.enums.StatutDevis;
import java.util.List;
import java.util.Optional;

public interface PropositionDevisRepository extends JpaRepository<PropositionDevis, Integer> {
    long countByProfessionnelIdAndStatut(Integer professionnelId, StatutDevis statut);

    List<PropositionDevis> findByProfessionnel_Id(Integer professionnelId);

    List<PropositionDevis> findByProfessionnel_IdAndStatut(Integer professionnelId, StatutDevis statut);

    List<PropositionDevis> findByNovice_Id(Integer noviceId);

    List<PropositionDevis> findByNovice_IdAndStatut(Integer noviceId, StatutDevis statut);

    long countByProfessionnel_IdAndDemande_Id(Integer professionnelId, Integer demandeId);

    Optional<PropositionDevis> findTopByProfessionnel_IdAndDemande_IdOrderByIdDesc(Integer professionnelId, Integer demandeId);

    long countByNovice_IdAndStatut(Integer noviceId, StatutDevis statut);
}
