package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.DemandeServiceRepository;
import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.Models.entites.DemandeService;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Models.enums.StatutDemande;
import com.ngaso.Ngaso.dto.DemandeUpdateResponse;
import com.ngaso.Ngaso.dto.DemandeProItemResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessionnelDemandeWorkflowService {

    private final DemandeServiceRepository demandeRepo;
    private final ProfessionnelRepository proRepo;
    private final NotificationService notificationService;

    public ProfessionnelDemandeWorkflowService(DemandeServiceRepository demandeRepo,
                                               ProfessionnelRepository proRepo,
                                               NotificationService notificationService) {
        this.demandeRepo = demandeRepo;
        this.proRepo = proRepo;
        this.notificationService = notificationService;
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
        // Notify novice that demande was accepted
        if (d.getNovice() != null) {
            notificationService.notify(d.getNovice(), com.ngaso.Ngaso.Models.enums.TypeNotification.DemandeService,
                    "Votre demande de service a été acceptée.");
        }
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
        // Notify novice that demande was refused
        if (d.getNovice() != null) {
            notificationService.notify(d.getNovice(), com.ngaso.Ngaso.Models.enums.TypeNotification.DemandeService,
                    "Votre demande de service a été refusée.");
        }
        DemandeUpdateResponse resp = new DemandeUpdateResponse();
        resp.setId(d.getId());
        resp.setStatut(d.getStatut());
        return resp;
    }

    @Transactional(readOnly = true)
    public List<DemandeProItemResponse> listDemandes(Integer professionnelId, StatutDemande statut) {
        Professionnel pro = proRepo.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(pro.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        List<DemandeService> demandes = (statut == null)
                ? demandeRepo.findByProfessionnel_Id(professionnelId)
                : demandeRepo.findByProfessionnel_IdAndStatut(professionnelId, statut);
        return demandes.stream().map(d -> new DemandeProItemResponse(
                d.getId(),
                d.getMessage(),
                d.getStatut(),
                d.getDateCréation(),
                d.getNovice() != null ? d.getNovice().getId() : null,
                d.getNovice() != null ? d.getNovice().getNom() : null,
                d.getNovice() != null ? d.getNovice().getPrenom() : null,
                d.getNovice() != null ? d.getNovice().getTelephone() : null,
                d.getEtape() != null ? d.getEtape().getIdEtape() : null,
                (d.getEtape() != null && d.getEtape().getProjet() != null) ? d.getEtape().getProjet().getIdProjet() : null,
                (d.getEtape() != null && d.getEtape().getProjet() != null) ? d.getEtape().getProjet().getTitre() : null,
                (d.getEtape() != null && d.getEtape().getModele() != null) ? d.getEtape().getModele().getOrdre() : null,
                (d.getEtape() != null && d.getEtape().getModele() != null) ? d.getEtape().getModele().getNom() : null
        )).collect(Collectors.toList());
    }
}
