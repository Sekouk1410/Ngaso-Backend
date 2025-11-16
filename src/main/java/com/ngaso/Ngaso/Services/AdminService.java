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
import com.ngaso.Ngaso.dto.ProfessionnelSummaryResponse;
import com.ngaso.Ngaso.dto.UtilisateurSummaryResponse;
import com.ngaso.Ngaso.dto.ModeleEtapeCreateRequest;
import com.ngaso.Ngaso.dto.ModeleEtapeResponse;
import com.ngaso.Ngaso.dto.IllustrationCreateRequest;
import com.ngaso.Ngaso.dto.IllustrationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<ProfessionnelSummaryResponse> listPendingProfessionnels() {
        return professionnelRepository.findByEstValider(false)
                .stream()
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

    @Transactional(readOnly = true)
    public List<UtilisateurSummaryResponse> listAllUsers() {
        return utilisateurRepository.findAll().stream()
                .map(this::toUserSummary)
                .collect(Collectors.toList());
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
        return new ModeleEtapeResponse(saved.getId(), saved.getNom(), saved.getDescription(), saved.getOrdre(), ids, labels);
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
}
