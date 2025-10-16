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

@Service
public class ProfessionnelPropositionService {

    private final ProfessionnelRepository professionnelRepository;
    private final ProjetConstructionRepository projetConstructionRepository;
    private final PropositionDevisRepository propositionDevisRepository;
    private final SpecialiteRepository specialiteRepository;
    private final FileStorageService fileStorageService;
    private final DemandeServiceRepository demandeServiceRepository;

    public ProfessionnelPropositionService(ProfessionnelRepository professionnelRepository,
                                           ProjetConstructionRepository projetConstructionRepository,
                                           PropositionDevisRepository propositionDevisRepository,
                                           SpecialiteRepository specialiteRepository,
                                           FileStorageService fileStorageService,
                                           DemandeServiceRepository demandeServiceRepository) {
        this.professionnelRepository = professionnelRepository;
        this.projetConstructionRepository = projetConstructionRepository;
        this.propositionDevisRepository = propositionDevisRepository;
        this.specialiteRepository = specialiteRepository;
        this.fileStorageService = fileStorageService;
        this.demandeServiceRepository = demandeServiceRepository;
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
        return saved;
    }
}
