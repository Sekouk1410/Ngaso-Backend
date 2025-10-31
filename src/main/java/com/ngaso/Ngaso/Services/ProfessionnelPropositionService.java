package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.DAO.PropositionDevisRepository;
import com.ngaso.Ngaso.DAO.ProjetConstructionRepository;
import com.ngaso.Ngaso.DAO.SpecialiteRepository;
import com.ngaso.Ngaso.DAO.DemandeServiceRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Models.entites.ProjetConstruction;
import com.ngaso.Ngaso.Models.entites.PropositionDevis;
import com.ngaso.Ngaso.Models.entites.Specialite;
import com.ngaso.Ngaso.Models.entites.DemandeService;
import com.ngaso.Ngaso.Models.enums.StatutDevis;
import com.ngaso.Ngaso.Models.enums.StatutDemande;
import com.ngaso.Ngaso.dto.CreatePropositionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ProfessionnelPropositionService {

    private final ProfessionnelRepository professionnelRepository;
    private final ProjetConstructionRepository projetConstructionRepository;
    private final PropositionDevisRepository propositionDevisRepository;
    private final SpecialiteRepository specialiteRepository;
    private final FileStorageService fileStorageService;
    private final DemandeServiceRepository demandeServiceRepository;
    private final NotificationService notificationService;

    public ProfessionnelPropositionService(ProfessionnelRepository professionnelRepository,
                                           ProjetConstructionRepository projetConstructionRepository,
                                           PropositionDevisRepository propositionDevisRepository,
                                           SpecialiteRepository specialiteRepository,
                                           FileStorageService fileStorageService,
                                           DemandeServiceRepository demandeServiceRepository,
                                           NotificationService notificationService) {
        this.professionnelRepository = professionnelRepository;
        this.projetConstructionRepository = projetConstructionRepository;
        this.propositionDevisRepository = propositionDevisRepository;
        this.specialiteRepository = specialiteRepository;
        this.fileStorageService = fileStorageService;
        this.demandeServiceRepository = demandeServiceRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PropositionDevis proposerPourProjet(Integer professionnelId, Integer projetId, CreatePropositionRequest req, MultipartFile devisFile) {
        Professionnel pro = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(pro.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        ProjetConstruction projet = projetConstructionRepository.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable"));
        if (projet.getDemande() == null) {
            // Créer une demande associant le novice et le professionnel proposant
            DemandeService demande = new DemandeService();
            demande.setNovice(projet.getProprietaire());
            demande.setProfessionnel(pro);
            demande.setDateCréation(new java.util.Date());
            demande.setStatut(StatutDemande.EN_ATTENTE);
            demande.setMessage("Demande générée automatiquement depuis une proposition de devis");
            DemandeService savedDemande = demandeServiceRepository.save(demande);
            projet.setDemande(savedDemande);
            projetConstructionRepository.save(projet);
        }

        // Règles d'envoi (par projet):
        // - max 2 propositions par pro pour le même projet (via la même demande liée au projet),
        // - une nouvelle proposition n'est possible que si la précédente a été REFUSER.
        Integer demandeId = projet.getDemande() != null ? projet.getDemande().getId() : null;
        if (demandeId != null) {
            long attempts = propositionDevisRepository.countByProfessionnel_IdAndDemande_Id(professionnelId, demandeId);
            if (attempts >= 2) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Vous avez déjà utilisé vos deux chances pour ce projet");
            }
            if (attempts >= 1) {
                var lastOpt = propositionDevisRepository.findTopByProfessionnel_IdAndDemande_IdOrderByIdDesc(professionnelId, demandeId);
                var last = lastOpt.orElse(null);
                if (last != null && last.getStatut() != StatutDevis.REFUSER) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous ne pouvez envoyer une nouvelle proposition que si la précédente a été rejetée");
                }
            }
        }

        PropositionDevis devis = new PropositionDevis();
        devis.setProfessionnel(pro);
        devis.setDemande(projet.getDemande());
        devis.setNovice(projet.getProprietaire());
        devis.setMontant(req.getMontant());
        devis.setDescription(req.getDescription());
        devis.setStatut(StatutDevis.EN_ATTENTE);
        if (devisFile != null && !devisFile.isEmpty()) {
            try {
                String path = fileStorageService.storeDevis(devisFile);
                devis.setFichierDevis(path);
            } catch (java.io.IOException e) {
                throw new IllegalArgumentException("Erreur lors de l'upload du document de devis");
            }
        }
        if (req.getSpecialiteId() != null) {
            Specialite s = specialiteRepository.findById(req.getSpecialiteId())
                    .orElseThrow(() -> new IllegalArgumentException("Spécialité introuvable"));
            devis.setSpecialite(s);
        }
        PropositionDevis saved = propositionDevisRepository.save(devis);
        // Notify novice about new proposition
        notificationService.notify(saved.getNovice(), com.ngaso.Ngaso.Models.enums.TypeNotification.PropositionDevis,
                "Nouvelle proposition de devis pour votre projet: " + (projet.getTitre() != null ? projet.getTitre() : String.valueOf(projet.getIdProjet())));
        return saved;
    }

    @Transactional
    public PropositionDevis annuler(Integer professionnelId, Integer propositionId) {
        Professionnel pro = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(pro.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        PropositionDevis p = propositionDevisRepository.findById(propositionId)
                .orElseThrow(() -> new IllegalArgumentException("Proposition introuvable"));
        if (p.getProfessionnel() == null || !p.getProfessionnel().getId().equals(professionnelId)) {
            throw new AccessDeniedException("Vous ne pouvez annuler que vos propres propositions");
        }
        if (p.getStatut() != StatutDevis.EN_ATTENTE) {
            throw new IllegalStateException("Seules les propositions en attente peuvent être annulées");
        }
        p.setStatut(StatutDevis.ANNULER);
        return propositionDevisRepository.save(p);
    }

    @Transactional(readOnly = true)
    public java.util.List<PropositionDevis> listMesPropositions(Integer professionnelId, StatutDevis statut) {
        Professionnel pro = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(pro.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        if (statut == null) {
            return propositionDevisRepository.findByProfessionnel_Id(professionnelId);
        }
        return propositionDevisRepository.findByProfessionnel_IdAndStatut(professionnelId, statut);
    }
}
