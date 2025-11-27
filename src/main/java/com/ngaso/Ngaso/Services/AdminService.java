package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.DAO.UtilisateurRepository;
import com.ngaso.Ngaso.DAO.ModeleEtapeRepository;
import com.ngaso.Ngaso.DAO.IllustrationRepository;
import com.ngaso.Ngaso.DAO.SpecialiteRepository;
import com.ngaso.Ngaso.DAO.ProjetConstructionRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Models.entites.Utilisateur;
import com.ngaso.Ngaso.Models.entites.ModeleEtape;
import com.ngaso.Ngaso.Models.entites.Illustration;
import com.ngaso.Ngaso.Models.entites.Specialite;
import com.ngaso.Ngaso.Models.enums.EtatProjet;
import com.ngaso.Ngaso.Models.enums.Role;
import com.ngaso.Ngaso.dto.ProfessionnelSummaryResponse;
import com.ngaso.Ngaso.dto.UtilisateurSummaryResponse;
import com.ngaso.Ngaso.dto.ModeleEtapeCreateRequest;
import com.ngaso.Ngaso.dto.ModeleEtapeResponse;
import com.ngaso.Ngaso.dto.IllustrationCreateRequest;
import com.ngaso.Ngaso.dto.IllustrationResponse;
import com.ngaso.Ngaso.dto.AdminDashboardStatsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Transactional
public class AdminService {

    private final ProfessionnelRepository professionnelRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ModeleEtapeRepository modeleEtapeRepository;
    private final IllustrationRepository illustrationRepository;
    private final SpecialiteRepository specialiteRepository;
    private final FileStorageService storageService;
    private final ProjetConstructionRepository projetConstructionRepository;

    public AdminService(ProfessionnelRepository professionnelRepository,
                        UtilisateurRepository utilisateurRepository,
                        ModeleEtapeRepository modeleEtapeRepository,
                        IllustrationRepository illustrationRepository,
                        SpecialiteRepository specialiteRepository,
                        FileStorageService storageService,
                        ProjetConstructionRepository projetConstructionRepository) {
        this.professionnelRepository = professionnelRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.modeleEtapeRepository = modeleEtapeRepository;
        this.illustrationRepository = illustrationRepository;
        this.specialiteRepository = specialiteRepository;
        this.storageService = storageService;
        this.projetConstructionRepository = projetConstructionRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardStatsResponse getDashboardStats() {
        LocalDate now = LocalDate.now();
        LocalDate firstDayCurrentMonth = now.withDayOfMonth(1);
        LocalDate firstDayPreviousMonth = firstDayCurrentMonth.minusMonths(1);

        Date currentMonthStart = Date.from(firstDayCurrentMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date nextMonthStart = Date.from(firstDayCurrentMonth.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date previousMonthStart = Date.from(firstDayPreviousMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        AdminDashboardStatsResponse resp = new AdminDashboardStatsResponse();
        // Utilisateurs totaux
        long totalUsers = utilisateurRepository.count();
        long usersCurrentMonth = utilisateurRepository.countRegisteredBetween(currentMonthStart, nextMonthStart);
        long usersPreviousMonth = utilisateurRepository.countRegisteredBetween(previousMonthStart, currentMonthStart);

        AdminDashboardStatsResponse.Metric mUsers = new AdminDashboardStatsResponse.Metric();
        mUsers.setValue(totalUsers);
        mUsers.setChangePercent(computeChangePercent(usersCurrentMonth, usersPreviousMonth));
        resp.setUtilisateursTotaux(mUsers);

        // Projets actifs (en cours)
        long activeProjects = projetConstructionRepository.countByEtat(EtatProjet.En_Cours);
        long activeCurrentMonth = projetConstructionRepository.countByEtatAndDateCreatedBetween(EtatProjet.En_Cours, currentMonthStart, nextMonthStart);
        long activePreviousMonth = projetConstructionRepository.countByEtatAndDateCreatedBetween(EtatProjet.En_Cours, previousMonthStart, currentMonthStart);

        AdminDashboardStatsResponse.Metric mActive = new AdminDashboardStatsResponse.Metric();
        mActive.setValue(activeProjects);
        mActive.setChangePercent(computeChangePercent(activeCurrentMonth, activePreviousMonth));
        resp.setProjetsActifs(mActive);

        // Projets ce mois
        long projetsCurrentMonth = projetConstructionRepository.countCreatedBetween(currentMonthStart, nextMonthStart);
        long projetsPreviousMonth = projetConstructionRepository.countCreatedBetween(previousMonthStart, currentMonthStart);

        AdminDashboardStatsResponse.Metric mMonth = new AdminDashboardStatsResponse.Metric();
        mMonth.setValue(projetsCurrentMonth);
        mMonth.setChangePercent(computeChangePercent(projetsCurrentMonth, projetsPreviousMonth));
        resp.setProjetsCeMois(mMonth);

        // Taux d'achèvement = projets terminés / total projets
        long totalProjects = projetConstructionRepository.count();
        long finishedProjects = projetConstructionRepository.countByEtat(EtatProjet.Termine);
        int completionRate = totalProjects > 0 ? (int) Math.round((finishedProjects * 100.0) / totalProjects) : 0;

        long totalProjectsPrev = projetConstructionRepository.countCreatedBetween(previousMonthStart, currentMonthStart);
        long finishedPrev = projetConstructionRepository.countByEtatAndDateCreatedBetween(EtatProjet.Termine, previousMonthStart, currentMonthStart);
        int completionRatePrev = totalProjectsPrev > 0 ? (int) Math.round((finishedPrev * 100.0) / totalProjectsPrev) : 0;

        AdminDashboardStatsResponse.RateMetric mCompletion = new AdminDashboardStatsResponse.RateMetric();
        mCompletion.setValue(completionRate);
        mCompletion.setChangePercent(computeChangePercent(completionRate, completionRatePrev));
        resp.setTauxAchevement(mCompletion);

        return resp;
    }

    private int computeChangePercent(long current, long previous) {
        if (previous <= 0) {
            return current > 0 ? 100 : 0;
        }
        return (int) Math.round(((current - previous) * 100.0) / previous);
    }

    @Transactional(readOnly = true)
    public Page<ProfessionnelSummaryResponse> listPendingProfessionnels(int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "dateInscription")
        );

        Page<Professionnel> prosPage = professionnelRepository.findByEstValider(false, pageable);

        List<ProfessionnelSummaryResponse> content = prosPage.stream()
                .map(p -> new ProfessionnelSummaryResponse(
                        p.getId(),
                        p.getNom(),
                        p.getPrenom(),
                        p.getTelephone(),
                        p.getAdresse(),
                        p.getEmail(),
                        p.getEntreprise(),
                        p.getDescription(),
                        p.getEstValider(),
                        p.getDocumentJustificatif(),
                        p.getSpecialite() != null ? p.getSpecialite().getId() : null,
                        p.getSpecialite() != null ? p.getSpecialite().getLibelle() : null,
                        p.getDateInscription()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, prosPage.getTotalElements());
    }

    public ProfessionnelSummaryResponse validateProfessionnel(Integer id) {
        Professionnel p = professionnelRepository.findById(id)
             .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable: " + id));
        p.setEstValider(true);
        Professionnel saved = professionnelRepository.save(p);
        return new ProfessionnelSummaryResponse(
                saved.getId(),
                saved.getNom(),
                saved.getPrenom(),
                saved.getTelephone(),
                saved.getAdresse(),
                saved.getEmail(),
                saved.getEntreprise(),
                saved.getDescription(),
                saved.getEstValider(),
                saved.getDocumentJustificatif(),
                saved.getSpecialite() != null ? saved.getSpecialite().getId() : null,
                saved.getSpecialite() != null ? saved.getSpecialite().getLibelle() : null,
                saved.getDateInscription()
        );
    }

    public ProfessionnelSummaryResponse rejectProfessionnel(Integer id) {
        Professionnel p = professionnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable: " + id));

        ProfessionnelSummaryResponse summary = new ProfessionnelSummaryResponse(
                p.getId(),
                p.getNom(),
                p.getPrenom(),
                p.getTelephone(),
                p.getAdresse(),
                p.getEmail(),
                p.getEntreprise(),
                p.getDescription(),
                p.getEstValider(),
                p.getDocumentJustificatif(),
                p.getSpecialite() != null ? p.getSpecialite().getId() : null,
                p.getSpecialite() != null ? p.getSpecialite().getLibelle() : null,
                p.getDateInscription()
        );

        professionnelRepository.delete(p);
        return summary;
    }

    @Transactional(readOnly = true)
    public Page<UtilisateurSummaryResponse> listAllUsers(int page, int size, Role role) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "dateInscription")
        );

        Page<Utilisateur> usersPage;
        if (role != null) {
            usersPage = utilisateurRepository.findByRole(role, pageable);
        } else {
            usersPage = utilisateurRepository.findAll(pageable);
        }

        List<UtilisateurSummaryResponse> content = usersPage.stream()
                .map(this::toUserSummary)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, usersPage.getTotalElements());
    }

    private UtilisateurSummaryResponse toUserSummary(Utilisateur u) {
        return new UtilisateurSummaryResponse(
                u.getId(),
                u.getNom(),
                u.getPrenom(),
                u.getTelephone(),
                u.getAdresse(),
                u.getEmail(),
                u.getRole(),
                u.getActif(),
                u.getDateInscription()
        );
    }

    public UtilisateurSummaryResponse disableUser(Integer id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));
        u.setActif(false);
        Utilisateur saved = utilisateurRepository.save(u);
        return toUserSummary(saved);
    }

    public UtilisateurSummaryResponse enableUser(Integer id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));
        u.setActif(true);
        Utilisateur saved = utilisateurRepository.save(u);
        return toUserSummary(saved);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<com.ngaso.Ngaso.dto.ProjetAdminItemResponse> listAllProjets(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                Math.max(page, 0), Math.max(size, 1),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "dateCréation"));

        org.springframework.data.domain.Page<com.ngaso.Ngaso.Models.entites.ProjetConstruction> projets = projetConstructionRepository
                .findAll(pageable);

        java.util.List<com.ngaso.Ngaso.dto.ProjetAdminItemResponse> content = projets.stream().map(p -> {
            com.ngaso.Ngaso.dto.ProjetAdminItemResponse r = new com.ngaso.Ngaso.dto.ProjetAdminItemResponse();
            r.setId(p.getIdProjet());
            r.setTitre(p.getTitre());
            if (p.getProprietaire() != null) {
                r.setProprietaireNom(p.getProprietaire().getNom());
                r.setProprietairePrenom(p.getProprietaire().getPrenom());
            }
            r.setLocalisation(p.getLocalisation());
            r.setBudget(p.getBudget());
            r.setDateCreation(p.getDateCréation());

            int total = p.getEtapes() != null ? p.getEtapes().size() : 0;
            int valides = p.getEtapes() != null
                    ? (int) p.getEtapes().stream().filter(e -> java.lang.Boolean.TRUE.equals(e.getEstValider())).count()
                    : 0;
            int percent = total > 0 ? (int) java.lang.Math.round((valides * 100.0) / total) : 0;
            r.setProgressPercent(percent);

            java.util.List<com.ngaso.Ngaso.Models.entites.EtapeConstruction> steps =
                    p.getEtapes() != null ? new java.util.ArrayList<>(p.getEtapes()) : java.util.Collections.emptyList();
            steps.sort((a, b) -> {
                java.lang.Integer o1 = a.getModele() != null ? a.getModele().getOrdre() : null;
                java.lang.Integer o2 = b.getModele() != null ? b.getModele().getOrdre() : null;
                if (o1 == null && o2 == null) return 0;
                if (o1 == null) return 1;
                if (o2 == null) return -1;
                return java.lang.Integer.compare(o1, o2);
            });
            int currentIdx = -1;
            for (int i = 0; i < steps.size(); i++) {
                if (!java.lang.Boolean.TRUE.equals(steps.get(i).getEstValider())) { currentIdx = i; break; }
            }
            if (currentIdx == -1 && !steps.isEmpty()) {
                currentIdx = steps.size() - 1;
            }
            String currentName = null;
            if (currentIdx >= 0 && currentIdx < steps.size()) {
                com.ngaso.Ngaso.Models.entites.ModeleEtape m = steps.get(currentIdx).getModele();
                currentName = m != null ? m.getNom() : null;
            }
            r.setCurrentEtape(currentName);

            return r;
        }).collect(java.util.stream.Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(
                content,
                pageable,
                projets.getTotalElements()
        );
    }

    // ====== Modele Etape ======
    public ModeleEtapeResponse createModeleEtape(ModeleEtapeCreateRequest req) {
        if (req.getNom() == null || req.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du modèle est requis");
        }
        if (req.getSpecialiteIds() == null || req.getSpecialiteIds().isEmpty()) {
            throw new IllegalArgumentException("Au moins une spécialité est requise");
        }
        List<Specialite> specs = specialiteRepository.findAllById(req.getSpecialiteIds());
        if (specs.isEmpty()) {
            throw new IllegalArgumentException("Spécialité(s) introuvable(s)");
        }
        ModeleEtape m = new ModeleEtape();
        m.setNom(req.getNom());
        m.setDescription(req.getDescription());
        m.setOrdre(req.getOrdre());
        m.setSpecialites(new java.util.HashSet<>(specs));
        ModeleEtape saved = modeleEtapeRepository.save(m);
        List<Integer> ids = saved.getSpecialites().stream().map(Specialite::getId).collect(Collectors.toList());
        List<String> labels = saved.getSpecialites().stream().map(Specialite::getLibelle).collect(Collectors.toList());
        long nombreIllustrations = saved.getIllustrations() != null ? saved.getIllustrations().size() : 0L;
        return new ModeleEtapeResponse(saved.getId(), saved.getNom(), saved.getDescription(), saved.getOrdre(), saved.getImageProfilUrl(), ids, labels, nombreIllustrations);
    }

    @Transactional(readOnly = true)
    public java.util.List<ModeleEtapeResponse> listModeleEtapes() {
        java.util.List<ModeleEtape> modeles = modeleEtapeRepository.findAllByOrderByOrdreAsc();
        return modeles.stream().map(m -> {
            java.util.List<Integer> ids = m.getSpecialites() != null
                    ? m.getSpecialites().stream().map(Specialite::getId).collect(Collectors.toList())
                    : java.util.Collections.emptyList();
            java.util.List<String> labels = m.getSpecialites() != null
                    ? m.getSpecialites().stream().map(Specialite::getLibelle).collect(Collectors.toList())
                    : java.util.Collections.emptyList();
            long nombreIllustrations = m.getIllustrations() != null ? m.getIllustrations().size() : 0L;
            return new ModeleEtapeResponse(
                    m.getId(),
                    m.getNom(),
                    m.getDescription(),
                    m.getOrdre(),
                    m.getImageProfilUrl(),
                    ids,
                    labels,
                    nombreIllustrations
            );
        }).collect(Collectors.toList());
    }

    public IllustrationResponse addIllustrationToModele(Integer modeleId, IllustrationCreateRequest req, org.springframework.web.multipart.MultipartFile image) {
        ModeleEtape modele = modeleEtapeRepository.findById(modeleId)
                .orElseThrow(() -> new IllegalArgumentException("Modèle d'étape introuvable: " + modeleId));
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image requise");
        }
        try {
            String publicUrl = storageService.storeIllustration(modeleId, image);
            Illustration ill = new Illustration();
            ill.setTitre(req.getTitre());
            ill.setDescription(req.getDescription());
            ill.setUrlImage(publicUrl);
            ill.setModele(modele);
            Illustration saved = illustrationRepository.save(ill);
            return new IllustrationResponse(saved.getId(), saved.getTitre(), saved.getDescription(), saved.getUrlImage(), modele.getId());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Echec d'enregistrement de l'image: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public java.util.List<IllustrationResponse> listIllustrationsForModele(Integer modeleId) {
        ModeleEtape modele = modeleEtapeRepository.findById(modeleId)
                .orElseThrow(() -> new IllegalArgumentException("Modèle d'étape introuvable: " + modeleId));
        java.util.List<Illustration> ills = modele.getIllustrations();
        if (ills == null) {
            return java.util.Collections.emptyList();
        }
        return ills.stream()
                .map(ill -> new IllustrationResponse(
                        ill.getId(),
                        ill.getTitre(),
                        ill.getDescription(),
                        ill.getUrlImage(),
                        modele.getId()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    public void deleteModeleEtape(Integer id) {
        ModeleEtape m = modeleEtapeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Modèle d'étape introuvable: " + id));
        modeleEtapeRepository.delete(m);
    }

    public void deleteIllustration(Integer id) {
        Illustration ill = illustrationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Illustration introuvable: " + id));
        String url = ill.getUrlImage();
        illustrationRepository.delete(ill);
        try {
            storageService.deleteByPublicUrl(url);
        } catch (Exception ignored) {
            // On ignore les erreurs de suppression de fichier pour ne pas bloquer la suppression logique
        }
    }

    @Transactional(readOnly = true)
    public String getModeleEtapeImageProfil(Integer modeleId) {
        ModeleEtape modele = modeleEtapeRepository.findById(modeleId)
                .orElseThrow(() -> new IllegalArgumentException("Modèle d'étape introuvable: " + modeleId));
        String url = modele.getImageProfilUrl();
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Aucune image de profil définie pour ce modèle d'étape");
        }
        return url;
    }

    public ModeleEtapeResponse updateModeleEtapeImageProfil(Integer modeleId, org.springframework.web.multipart.MultipartFile image) {
        ModeleEtape modele = modeleEtapeRepository.findById(modeleId)
                .orElseThrow(() -> new IllegalArgumentException("Modèle d'étape introuvable: " + modeleId));
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image requise");
        }
        try {
            String publicUrl = storageService.storeIllustration(modeleId, image);
            modele.setImageProfilUrl(publicUrl);
            ModeleEtape saved = modeleEtapeRepository.save(modele);

            java.util.List<Integer> ids = saved.getSpecialites() != null
                    ? saved.getSpecialites().stream().map(Specialite::getId).collect(java.util.stream.Collectors.toList())
                    : java.util.Collections.emptyList();
            java.util.List<String> labels = saved.getSpecialites() != null
                    ? saved.getSpecialites().stream().map(Specialite::getLibelle).collect(java.util.stream.Collectors.toList())
                    : java.util.Collections.emptyList();
            long nombreIllustrations = saved.getIllustrations() != null ? saved.getIllustrations().size() : 0L;

            return new ModeleEtapeResponse(
                    saved.getId(),
                    saved.getNom(),
                    saved.getDescription(),
                    saved.getOrdre(),
                    saved.getImageProfilUrl(),
                    ids,
                    labels,
                    nombreIllustrations
            );
        } catch (Exception ex) {
            throw new IllegalArgumentException("Echec d'enregistrement de l'image de profil: " + ex.getMessage());
        }
    }
}
