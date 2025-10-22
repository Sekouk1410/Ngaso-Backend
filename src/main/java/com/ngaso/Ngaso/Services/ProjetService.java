package com.ngaso.Ngaso.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ngaso.Ngaso.DAO.EtapeConstructionRepository;
import com.ngaso.Ngaso.DAO.ModeleEtapeRepository;
import com.ngaso.Ngaso.DAO.NoviceRepository;
import com.ngaso.Ngaso.DAO.ProjetConstructionRepository;
import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.DAO.DemandeServiceRepository;
import com.ngaso.Ngaso.DAO.PropositionDevisRepository;
import com.ngaso.Ngaso.Models.entites.Novice;
import com.ngaso.Ngaso.Models.entites.ProjetConstruction;
import com.ngaso.Ngaso.Models.entites.ModeleEtape;
import com.ngaso.Ngaso.Models.entites.EtapeConstruction;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Models.entites.DemandeService;
import com.ngaso.Ngaso.Models.entites.PropositionDevis;

import com.ngaso.Ngaso.Models.enums.EtatProjet;
import com.ngaso.Ngaso.Models.enums.StatutDemande;
import com.ngaso.Ngaso.Models.enums.StatutDevis;
import com.ngaso.Ngaso.dto.ProjetCreateRequest;
import com.ngaso.Ngaso.dto.ProjetResponse;
import com.ngaso.Ngaso.dto.EtapeWithIllustrationsResponse;
import com.ngaso.Ngaso.dto.IllustrationResponse;
import com.ngaso.Ngaso.dto.ProjetUpdateRequest;
import com.ngaso.Ngaso.dto.ProfessionnelBriefResponse;
import com.ngaso.Ngaso.dto.DemandeCreateRequest;
import com.ngaso.Ngaso.dto.DemandeBriefResponse;
import com.ngaso.Ngaso.dto.DemandeProjectItemResponse;
import com.ngaso.Ngaso.dto.PropositionDevisResponse;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjetService {

    private final ProjetConstructionRepository projetRepo;
    private final NoviceRepository noviceRepo;
    private final ModeleEtapeRepository modeleEtapeRepo;
    private final EtapeConstructionRepository etapeRepo;
    private final ProfessionnelRepository professionnelRepo;
    private final DemandeServiceRepository demandeRepo;
    private final PropositionDevisRepository propositionRepo;
    private final ConversationService conversationService;
    private final NotificationService notificationService;

    public ProjetService(
            ProjetConstructionRepository projetRepo,
            NoviceRepository noviceRepo,
            ModeleEtapeRepository modeleEtapeRepo,
            EtapeConstructionRepository etapeRepo,
            ProfessionnelRepository professionnelRepo,
            DemandeServiceRepository demandeRepo,
            PropositionDevisRepository propositionRepo,
            ConversationService conversationService,
            NotificationService notificationService
    ) {
        this.projetRepo = projetRepo;
        this.noviceRepo = noviceRepo;
        this.modeleEtapeRepo = modeleEtapeRepo;
        this.etapeRepo = etapeRepo;
        this.professionnelRepo = professionnelRepo;
        this.demandeRepo = demandeRepo;
        this.propositionRepo = propositionRepo;
        this.conversationService = conversationService;
        this.notificationService = notificationService;
    }

    public ProjetResponse createProjet(ProjetCreateRequest req) {
        Novice proprietaire = noviceRepo.findById(req.getNoviceId())
                .orElseThrow(() -> new IllegalArgumentException("Novice introuvable: " + req.getNoviceId()));

        ProjetConstruction p = new ProjetConstruction();
        p.setTitre(req.getTitre());
        p.setDimensionsTerrain(req.getDimensionsTerrain());
        p.setBudget(req.getBudget());
        p.setLocalisation(req.getLocalisation());
        p.setEtat(EtatProjet.En_Cours);
        p.setDateCréation(new Date());
        p.setProprietaire(proprietaire);

        ProjetConstruction saved = projetRepo.save(p);

        // Initialiser les étapes à partir des modèles (ordre croissant)
        List<ModeleEtape> modeles = modeleEtapeRepo.findAllByOrderByOrdreAsc();

        for (ModeleEtape m : modeles) {
            EtapeConstruction etape = new EtapeConstruction();
            etape.setProjet(saved);
            etape.setModele(m);
            etape.setEstValider(false);
            EtapeConstruction etapeSaved = etapeRepo.save(etape);
            saved.getEtapes().add(etapeSaved);
        }

        return map(saved);
    }

    public ProjetResponse createProjetForNovice(Integer noviceId, ProjetCreateRequest req) {
        Novice proprietaire = noviceRepo.findById(noviceId)
                .orElseThrow(() -> new IllegalArgumentException("Novice introuvable: " + noviceId));

        ProjetConstruction p = new ProjetConstruction();
        p.setTitre(req.getTitre());
        p.setDimensionsTerrain(req.getDimensionsTerrain());
        p.setBudget(req.getBudget());
        p.setLocalisation(req.getLocalisation());
        p.setEtat(EtatProjet.En_Cours);
        p.setDateCréation(new Date());
        p.setProprietaire(proprietaire);

        ProjetConstruction saved = projetRepo.save(p);

        // Initialiser les étapes à partir des modèles (ordre croissant)
        List<ModeleEtape> modeles = modeleEtapeRepo.findAllByOrderByOrdreAsc();

        for (ModeleEtape m : modeles) {
            EtapeConstruction etape = new EtapeConstruction();
            etape.setProjet(saved);
            etape.setModele(m);
            etape.setEstValider(false);
            etapeRepo.save(etape);
            saved.getEtapes().add(etape);
        }

        return map(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjetResponse> listByNovice(Integer noviceId) {
        return projetRepo.findByProprietaire_Id(noviceId)
                .stream().map(this::map).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjetResponse> listEnCours() {
        return projetRepo.findByEtat(EtatProjet.En_Cours)
                .stream().map(this::map).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjetResponse getProjet(Integer id) {
        Optional<ProjetConstruction> opt = projetRepo.findById(id);
        ProjetConstruction p = opt.orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + id));
        return map(p);
    }

    @Transactional(readOnly = true)
    public ProjetResponse getProjetOwned(Integer authUserId, Integer id) {
        ProjetConstruction p = projetRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + id));
        if (p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: accès restreint à vos projets");
        }
        return map(p);
    }

    @Transactional(readOnly = true)
    public List<EtapeWithIllustrationsResponse> listEtapesWithIllustrations(Integer projetId) {
        ProjetConstruction p = projetRepo.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + projetId));
        return p.getEtapes().stream()
                .sorted((e1, e2) -> {
                    Integer o1 = e1.getModele() != null ? e1.getModele().getOrdre() : Integer.MAX_VALUE;
                    Integer o2 = e2.getModele() != null ? e2.getModele().getOrdre() : Integer.MAX_VALUE;
                    return Integer.compare(o1 == null ? Integer.MAX_VALUE : o1, o2 == null ? Integer.MAX_VALUE : o2);
                })
                .map(e -> {
                    ModeleEtape m = e.getModele();
                    List<IllustrationResponse> ill = m != null && m.getIllustrations() != null
                            ? m.getIllustrations().stream()
                                .map(i -> new IllustrationResponse(i.getId(), i.getTitre(), i.getDescription(), i.getUrlImage(), m.getId()))
                                .collect(Collectors.toList())
                            : java.util.Collections.emptyList();
                    return new EtapeWithIllustrationsResponse(
                            e.getIdEtape(),
                            m != null ? m.getId() : null,
                            m != null ? m.getNom() : null,
                            m != null ? m.getDescription() : null,
                            m != null ? m.getOrdre() : null,
                            e.getEstValider(),
                            ill
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EtapeWithIllustrationsResponse> listEtapesWithIllustrationsOwned(Integer authUserId, Integer projetId) {
        ProjetConstruction p = projetRepo.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + projetId));
        if (p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: accès restreint à vos projets");
        }
        return listEtapesWithIllustrations(projetId);
    }

    @Transactional(readOnly = true)
    public List<ProfessionnelBriefResponse> listProfessionnelsForEtapeOwned(Integer authUserId, Integer etapeId) {
        EtapeConstruction e = etapeRepo.findById(etapeId)
                .orElseThrow(() -> new IllegalArgumentException("Étape introuvable: " + etapeId));
        ProjetConstruction p = e.getProjet();
        if (p == null || p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: cette étape n'appartient pas à votre projet");
        }
        ModeleEtape m = e.getModele();
        if (m == null || m.getSpecialite() == null || m.getSpecialite().getId() == null) {
            return java.util.Collections.emptyList();
        }
        Integer specialiteId = m.getSpecialite().getId();
        return professionnelRepo.findBySpecialite_IdAndEstValiderTrue(specialiteId)
                .stream()
                .map(pro -> new ProfessionnelBriefResponse(
                        pro.getId(),
                        pro.getNom(),
                        pro.getPrenom(),
                        pro.getTelephone(),
                        pro.getEmail(),
                        pro.getEntreprise(),
                        pro.getSpecialite() != null ? pro.getSpecialite().getId() : null,
                        pro.getSpecialite() != null ? pro.getSpecialite().getLibelle() : null,
                        pro.getRealisations()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Integer createDemandeForEtapeOwned(Integer authUserId, Integer etapeId, DemandeCreateRequest req) {
        EtapeConstruction e = etapeRepo.findById(etapeId)
                .orElseThrow(() -> new IllegalArgumentException("Étape introuvable: " + etapeId));
        ProjetConstruction p = e.getProjet();
        if (p == null || p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: cette étape n'appartient pas à votre projet");
        }
        ModeleEtape m = e.getModele();
        if (m == null || m.getSpecialite() == null || m.getSpecialite().getId() == null) {
            throw new IllegalStateException("Cette étape n'a pas de spécialité assignée");
        }
        Professionnel pro = professionnelRepo.findById(req.getProfessionnelId())
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(pro.getEstValider())) {
            throw new org.springframework.security.access.AccessDeniedException("Professionnel non validé");
        }
        Integer etapeSpecId = m.getSpecialite().getId();
        Integer proSpecId = pro.getSpecialite() != null ? pro.getSpecialite().getId() : null;
        if (proSpecId == null || !proSpecId.equals(etapeSpecId)) {
            throw new IllegalArgumentException("La spécialité du professionnel ne correspond pas à celle de l'étape");
        }
        // Empêcher les doublons EN_ATTENTE pour la même étape, même pro et même novice
        boolean alreadyPending = demandeRepo.existsByEtape_IdEtapeAndNovice_IdAndProfessionnel_IdAndStatut(
                etapeId, authUserId, req.getProfessionnelId(), StatutDemande.EN_ATTENTE);
        if (alreadyPending) {
            throw new IllegalStateException("Votre demande a déjà été envoyée à ce professionnel pour cette étape. Veuillez attendre sa réponse.");
        }
        DemandeService d = new DemandeService();
        d.setEtape(e);
        d.setNovice(p.getProprietaire());
        d.setProfessionnel(pro);
        d.setMessage(req.getMessage());
        d.setStatut(StatutDemande.EN_ATTENTE);
        d.setDateCréation(new Date());
        DemandeService saved = demandeRepo.save(d);
        // Notify professional about new service request
        notificationService.notify(pro, com.ngaso.Ngaso.Models.enums.TypeNotification.DemandeService,
                "Nouvelle demande de service pour l'étape " + (m != null ? m.getNom() : String.valueOf(e.getIdEtape())));
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public List<DemandeBriefResponse> listDemandesForEtapeOwned(Integer authUserId, Integer etapeId) {
        EtapeConstruction e = etapeRepo.findById(etapeId)
                .orElseThrow(() -> new IllegalArgumentException("Étape introuvable: " + etapeId));
        ProjetConstruction p = e.getProjet();
        if (p == null || p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: cette étape n'appartient pas à votre projet");
        }
        return demandeRepo.findByEtape_IdEtapeAndNovice_Id(etapeId, authUserId)
                .stream()
                .map(d -> new DemandeBriefResponse(
                        d.getId(),
                        d.getMessage(),
                        d.getStatut(),
                        d.getDateCréation(),
                        d.getProfessionnel() != null ? d.getProfessionnel().getId() : null,
                        d.getProfessionnel() != null ? d.getProfessionnel().getNom() : null,
                        d.getProfessionnel() != null ? d.getProfessionnel().getPrenom() : null,
                        d.getProfessionnel() != null ? d.getProfessionnel().getEntreprise() : null
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DemandeProjectItemResponse> listDemandesForProjetOwned(Integer authUserId, Integer projetId) {
        ProjetConstruction p = projetRepo.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + projetId));
        if (p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: accès restreint à vos projets");
        }
        return demandeRepo.findByEtape_Projet_IdProjetAndNovice_Id(projetId, authUserId)
                .stream()
                .map(d -> new DemandeProjectItemResponse(
                        d.getId(),
                        d.getMessage(),
                        d.getStatut(),
                        d.getDateCréation(),
                        d.getProfessionnel() != null ? d.getProfessionnel().getId() : null,
                        d.getProfessionnel() != null ? d.getProfessionnel().getNom() : null,
                        d.getProfessionnel() != null ? d.getProfessionnel().getPrenom() : null,
                        d.getProfessionnel() != null ? d.getProfessionnel().getEntreprise() : null,
                        d.getEtape() != null ? d.getEtape().getIdEtape() : null,
                        (d.getEtape() != null && d.getEtape().getModele() != null) ? d.getEtape().getModele().getOrdre() : null,
                        (d.getEtape() != null && d.getEtape().getModele() != null) ? d.getEtape().getModele().getNom() : null
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void cancelDemandeOwned(Integer authUserId, Integer demandeId) {
        DemandeService d = demandeRepo.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable: " + demandeId));
        if (d.getNovice() == null || d.getNovice().getId() == null || !d.getNovice().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: vous ne pouvez annuler que vos demandes");
        }
        if (d.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Seules les demandes en attente peuvent être annulées");
        }
        d.setStatut(StatutDemande.ANNULER);
        demandeRepo.save(d);
    }

    // ====== Propositions - côté Novice ======
    @Transactional(readOnly = true)
    public java.util.List<com.ngaso.Ngaso.dto.PropositionDevisResponse> listPropositionsOwned(Integer authUserId, StatutDevis statut) {
        java.util.List<PropositionDevis> list;
        if (statut == null) {
            list = propositionRepo.findByNovice_Id(authUserId);
        } else {
            list = propositionRepo.findByNovice_IdAndStatut(authUserId, statut);
        }
        return list.stream().map(this::map).collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public PropositionDevisResponse accepterPropositionOwned(Integer authUserId, Integer propositionId) {
        PropositionDevis p = propositionRepo.findById(propositionId)
                .orElseThrow(() -> new IllegalArgumentException("Proposition introuvable: " + propositionId));
        if (p.getNovice() == null || p.getNovice().getId() == null || !p.getNovice().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: vous ne pouvez agir que sur vos propositions");
        }
        if (p.getStatut() != StatutDevis.EN_ATTENTE) {
            throw new IllegalStateException("Seules les propositions en attente peuvent être acceptées");
        }
        p.setStatut(StatutDevis.ACCEPTER);
        PropositionDevis saved = propositionRepo.save(p);
        // Ouvrir/Créer la conversation liée à cette proposition acceptée
        conversationService.openOrCreateForProposition(saved);
        // Notify professional that proposition is accepted
        if (saved.getProfessionnel() != null) {
            notificationService.notify(saved.getProfessionnel(), com.ngaso.Ngaso.Models.enums.TypeNotification.PropositionDevis,
                    "Votre proposition de devis a été acceptée.");
        }
        return map(saved);
    }

    @Transactional
    public PropositionDevisResponse refuserPropositionOwned(Integer authUserId, Integer propositionId) {
        PropositionDevis p = propositionRepo.findById(propositionId)
                .orElseThrow(() -> new IllegalArgumentException("Proposition introuvable: " + propositionId));
        if (p.getNovice() == null || p.getNovice().getId() == null || !p.getNovice().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: vous ne pouvez agir que sur vos propositions");
        }
        if (p.getStatut() != StatutDevis.EN_ATTENTE) {
            throw new IllegalStateException("Seules les propositions en attente peuvent être refusées");
        }
        p.setStatut(StatutDevis.REFUSER);
        PropositionDevis saved = propositionRepo.save(p);
        // Notify professional that proposition is refused
        if (saved.getProfessionnel() != null) {
            notificationService.notify(saved.getProfessionnel(), com.ngaso.Ngaso.Models.enums.TypeNotification.PropositionDevis,
                    "Votre proposition de devis a été refusée.");
        }
        return map(saved);
    }

    public EtapeWithIllustrationsResponse validateEtapeByOwner(Integer authUserId, Integer etapeId) {
        EtapeConstruction e = etapeRepo.findById(etapeId)
                .orElseThrow(() -> new IllegalArgumentException("Étape introuvable: " + etapeId));
        ProjetConstruction p = e.getProjet();
        if (p == null || p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: cette étape n'appartient pas à votre projet");
        }
        // Règle: validation séquentielle selon l'ordre du modèle
        ModeleEtape currentModel = e.getModele();
        Integer currentOrder = currentModel != null ? currentModel.getOrdre() : null;
        if (currentOrder != null && p.getEtapes() != null) {
            boolean previousAllValidated = p.getEtapes().stream()
                    .filter(other -> other.getIdEtape() != null && !other.getIdEtape().equals(e.getIdEtape()))
                    .filter(other -> other.getModele() != null && other.getModele().getOrdre() != null)
                    .filter(other -> other.getModele().getOrdre() < currentOrder)
                    .allMatch(other -> Boolean.TRUE.equals(other.getEstValider()));
            if (!previousAllValidated) {
                throw new IllegalStateException("Impossible de valider cette étape avant de valider les étapes précédentes");
            }
        }
        e.setEstValider(true);
        EtapeConstruction saved = etapeRepo.save(e);
        ModeleEtape m = saved.getModele();
        List<IllustrationResponse> ill = m != null && m.getIllustrations() != null
                ? m.getIllustrations().stream()
                    .map(i -> new IllustrationResponse(i.getId(), i.getTitre(), i.getDescription(), i.getUrlImage(), m.getId()))
                    .collect(Collectors.toList())
                : java.util.Collections.emptyList();
        return new EtapeWithIllustrationsResponse(
                saved.getIdEtape(),
                m != null ? m.getId() : null,
                m != null ? m.getNom() : null,
                m != null ? m.getDescription() : null,
                m != null ? m.getOrdre() : null,
                saved.getEstValider(),
                ill
        );
    }

    private ProjetResponse map(ProjetConstruction p) {
        ProjetResponse r = new ProjetResponse();
        r.setId(p.getIdProjet());
        r.setTitre(p.getTitre());
        r.setBudget(p.getBudget());
        r.setLocalisation(p.getLocalisation());
        r.setDimensionsTerrain(p.getDimensionsTerrain());
        r.setEtat(p.getEtat());
        r.setDateCreation(p.getDateCréation());
        if (p.getEtapes() != null) {
            int total = p.getEtapes().size();
            int valides = (int) p.getEtapes().stream().filter(e -> Boolean.TRUE.equals(e.getEstValider())).count();
            r.setTotalEtapes(total);
            r.setEtapesValidees(valides);
        } else {
            r.setTotalEtapes(0);
            r.setEtapesValidees(0);
        }
        return r;
    }

    private PropositionDevisResponse map(PropositionDevis d) {
        PropositionDevisResponse r = new PropositionDevisResponse();
        r.setId(d.getId());
        r.setMontant(d.getMontant());
        r.setDescription(d.getDescription());
        r.setFichierDevis(d.getFichierDevis());
        r.setStatut(d.getStatut());
        if (d.getSpecialite() != null) {
            r.setSpecialiteId(d.getSpecialite().getId());
        }
        if (d.getProfessionnel() != null) {
            var pro = d.getProfessionnel();
            com.ngaso.Ngaso.dto.ProfessionnelBriefResponse pb = new com.ngaso.Ngaso.dto.ProfessionnelBriefResponse(
                    pro.getId(),
                    pro.getNom(),
                    pro.getPrenom(),
                    pro.getTelephone(),
                    pro.getEmail(),
                    pro.getEntreprise(),
                    pro.getSpecialite() != null ? pro.getSpecialite().getId() : null,
                    pro.getSpecialite() != null ? pro.getSpecialite().getLibelle() : null,
                    pro.getRealisations()
            );
            r.setProfessionnel(pb);
        }
        return r;
    }

    // ====== Update/Delete by owner (novice) ======
    public ProjetResponse updateProjetByOwner(Integer authUserId, Integer projetId, ProjetUpdateRequest req) {
        ProjetConstruction p = projetRepo.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + projetId));
        if (p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: vous n'êtes pas le propriétaire du projet");
        }
        if (req.getTitre() != null) p.setTitre(req.getTitre());
        if (req.getDimensionsTerrain() != null) p.setDimensionsTerrain(req.getDimensionsTerrain());
        if (req.getBudget() != null) p.setBudget(req.getBudget());
        if (req.getLocalisation() != null) p.setLocalisation(req.getLocalisation());
        ProjetConstruction saved = projetRepo.save(p);
        return map(saved);
    }

    public void deleteProjetByOwner(Integer authUserId, Integer projetId) {
        ProjetConstruction p = projetRepo.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + projetId));
        if (p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: vous n'êtes pas le propriétaire du projet");
        }
        projetRepo.delete(p);
    }
}
