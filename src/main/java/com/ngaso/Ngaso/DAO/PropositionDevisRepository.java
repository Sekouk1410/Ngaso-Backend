package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.PropositionDevis;
import com.ngaso.Ngaso.Models.enums.StatutDevis;

public interface PropositionDevisRepository extends JpaRepository<PropositionDevis, Integer> {
    long countByProfessionnelIdAndStatut(Integer professionnelId, StatutDevis statut);
}
