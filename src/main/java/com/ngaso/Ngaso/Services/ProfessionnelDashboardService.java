package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.DemandeServiceRepository;
import com.ngaso.Ngaso.DAO.MessageRepository;
import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.DAO.PropositionDevisRepository;
import com.ngaso.Ngaso.DAO.ProjetConstructionRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Models.entites.ProjetConstruction;
import com.ngaso.Ngaso.Models.enums.StatutDevis;
import com.ngaso.Ngaso.dto.ProfessionnelDashboardResponse;
import com.ngaso.Ngaso.dto.ProjetBrief;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.PageRequest;

@Service
@Transactional(readOnly = true)
public class ProfessionnelDashboardService {

    private final ProfessionnelRepository professionnelRepository;
    private final PropositionDevisRepository propositionDevisRepository;
    private final DemandeServiceRepository demandeServiceRepository;
    private final MessageRepository messageRepository;
    private final ProjetConstructionRepository projetConstructionRepository;

    public ProfessionnelDashboardService(ProfessionnelRepository professionnelRepository,
                                         PropositionDevisRepository propositionDevisRepository,
                                         DemandeServiceRepository demandeServiceRepository,
                                         MessageRepository messageRepository,
                                         ProjetConstructionRepository projetConstructionRepository) {
        this.professionnelRepository = professionnelRepository;
        this.propositionDevisRepository = propositionDevisRepository;
        this.demandeServiceRepository = demandeServiceRepository;
        this.messageRepository = messageRepository;
        this.projetConstructionRepository = projetConstructionRepository;
    }

    public ProfessionnelDashboardResponse getDashboard(Integer professionnelId) {
        Professionnel p = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(p.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }

        long propositionsEnAttente = propositionDevisRepository
                .countByProfessionnelIdAndStatut(professionnelId, StatutDevis.EN_ATTENTE);
        long propositionsValidees = propositionDevisRepository
                .countByProfessionnelIdAndStatut(professionnelId, StatutDevis.ACCEPTER);

        long demandesTotal = demandeServiceRepository.countByProfessionnelId(professionnelId);
        long messagesNonLus = messageRepository.countUnreadForProfessionnel(professionnelId);

        var derniersProjetsEntities = projetConstructionRepository
                .findLastGlobal(PageRequest.of(0, 3));
        java.util.List<ProjetBrief> derniersProjets = new java.util.ArrayList<>();
        for (ProjetConstruction proj : derniersProjetsEntities) {
            ProjetBrief pb = new ProjetBrief();
            pb.setId(proj.getIdProjet());
            pb.setTitre(proj.getTitre());
            pb.setDateCreation(proj.getDateCréation());
            pb.setLocalisation(proj.getLocalisation());
            pb.setBudget(proj.getBudget());
            if (proj.getProprietaire() != null) {
                pb.setProprietaireNom(proj.getProprietaire().getNom());
            }
            derniersProjets.add(pb);
        }

        ProfessionnelDashboardResponse resp = new ProfessionnelDashboardResponse();
        resp.setPrenom(p.getPrenom());
        resp.setMessageBienvenue("Bienvenue, " + p.getPrenom() + " !");
        resp.setPropositionsEnAttente(propositionsEnAttente);
        resp.setPropositionsValidees(propositionsValidees);
        resp.setDemandesTotal(demandesTotal);
        resp.setMessagesNonLus(messagesNonLus);
        resp.setRealisations(p.getRealisations());
        resp.setDerniersProjets(derniersProjets);
        return resp;
    }
}
