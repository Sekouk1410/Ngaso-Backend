package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.DemandeServiceRepository;
import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.Models.entites.DemandeService;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Models.enums.StatutDemande;
import com.ngaso.Ngaso.dto.DemandeUpdateResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessionnelDemandeWorkflowService {

    private final DemandeServiceRepository demandeRepo;
    private final ProfessionnelRepository proRepo;

    public ProfessionnelDemandeWorkflowService(DemandeServiceRepository demandeRepo,
                                               ProfessionnelRepository proRepo) {
        this.demandeRepo = demandeRepo;
        this.proRepo = proRepo;
    }

    @Transactional
    public DemandeUpdateResponse valider(Integer professionnelId, Integer demandeId) {
        Professionnel pro = proRepo.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(pro.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        DemandeService d = demandeRepo.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));
        if (d.getProfessionnel() == null || !d.getProfessionnel().getId().equals(professionnelId)) {
            throw new AccessDeniedException("Vous ne pouvez agir que sur vos demandes");
        }
        if (d.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalArgumentException("Seules les demandes en attente peuvent être validées");
        }
        d.setStatut(StatutDemande.ACCEPTER);
        demandeRepo.save(d);
        DemandeUpdateResponse resp = new DemandeUpdateResponse();
        resp.setId(d.getId());
        resp.setStatut(d.getStatut());
        return resp;
    }

    @Transactional
    public DemandeUpdateResponse refuser(Integer professionnelId, Integer demandeId) {
        Professionnel pro = proRepo.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(pro.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        DemandeService d = demandeRepo.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));
        if (d.getProfessionnel() == null || !d.getProfessionnel().getId().equals(professionnelId)) {
            throw new AccessDeniedException("Vous ne pouvez agir que sur vos demandes");
        }
        if (d.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalArgumentException("Seules les demandes en attente peuvent être refusées");
        }
        d.setStatut(StatutDemande.REFUSER);
        demandeRepo.save(d);
        DemandeUpdateResponse resp = new DemandeUpdateResponse();
        resp.setId(d.getId());
        resp.setStatut(d.getStatut());
        return resp;
    }
}
