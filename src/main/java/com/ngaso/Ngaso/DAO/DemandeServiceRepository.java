package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.DemandeService;
import com.ngaso.Ngaso.Models.enums.StatutDemande;

public interface DemandeServiceRepository extends JpaRepository<DemandeService, Integer> {
    long countByProfessionnelId(Integer professionnelId);
    long countByProfessionnelIdAndStatut(Integer professionnelId, StatutDemande statut);
}
