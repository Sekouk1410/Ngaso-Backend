package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.DemandeServiceRepository;
import com.ngaso.Ngaso.DAO.MessageRepository;
import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.DAO.PropositionDevisRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Models.enums.StatutDevis;
import com.ngaso.Ngaso.dto.ProfessionnelDashboardResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProfessionnelDashboardService {

    private final ProfessionnelRepository professionnelRepository;
    private final PropositionDevisRepository propositionDevisRepository;
    private final DemandeServiceRepository demandeServiceRepository;
    private final MessageRepository messageRepository;

    public ProfessionnelDashboardService(ProfessionnelRepository professionnelRepository,
                                         PropositionDevisRepository propositionDevisRepository,
                                         DemandeServiceRepository demandeServiceRepository,
                                         MessageRepository messageRepository) {
        this.professionnelRepository = professionnelRepository;
        this.propositionDevisRepository = propositionDevisRepository;
        this.demandeServiceRepository = demandeServiceRepository;
        this.messageRepository = messageRepository;
    }

    public ProfessionnelDashboardResponse getDashboard(Integer professionnelId) {
        Professionnel p = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));

        long propositionsEnAttente = propositionDevisRepository
                .countByProfessionnelIdAndStatut(professionnelId, StatutDevis.EN_ATTENTE);
        long propositionsValidees = propositionDevisRepository
                .countByProfessionnelIdAndStatut(professionnelId, StatutDevis.ACCEPTER);

        long demandesTotal = demandeServiceRepository.countByProfessionnelId(professionnelId);
        long messagesNonLus = messageRepository.countUnreadForProfessionnel(professionnelId);

        ProfessionnelDashboardResponse resp = new ProfessionnelDashboardResponse();
        resp.setPrenom(p.getPrenom());
        resp.setMessageBienvenue("Bienvenue, " + p.getPrenom() + " !");
        resp.setPropositionsEnAttente(propositionsEnAttente);
        resp.setPropositionsValidees(propositionsValidees);
        resp.setDemandesTotal(demandesTotal);
        resp.setMessagesNonLus(messagesNonLus);
        resp.setRealisations(p.getRealisations());
        return resp;
    }
}
