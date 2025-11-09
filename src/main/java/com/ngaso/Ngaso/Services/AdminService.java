package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.DAO.UtilisateurRepository;
import com.ngaso.Ngaso.DAO.ModeleEtapeRepository;
import com.ngaso.Ngaso.DAO.IllustrationRepository;
import com.ngaso.Ngaso.DAO.SpecialiteRepository;
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
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
@Transactional
public class AdminService {

    private final ProfessionnelRepository professionnelRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ModeleEtapeRepository modeleEtapeRepository;
    private final IllustrationRepository illustrationRepository;
    private final SpecialiteRepository specialiteRepository;

    public AdminService(ProfessionnelRepository professionnelRepository,
                        UtilisateurRepository utilisateurRepository,
                        ModeleEtapeRepository modeleEtapeRepository,
                        IllustrationRepository illustrationRepository,
                        SpecialiteRepository specialiteRepository) {
        this.professionnelRepository = professionnelRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.modeleEtapeRepository = modeleEtapeRepository;
        this.illustrationRepository = illustrationRepository;
        this.specialiteRepository = specialiteRepository;
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
                        p.getSpecialite() != null ? p.getSpecialite().getLibelle() : null
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
                saved.getSpecialite() != null ? saved.getSpecialite().getLibelle() : null
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
                u.getActif()
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
            throw new IllegalArgumentException("Aucune des spécialités fournies n'a été trouvée");
        }
        ModeleEtape m = new ModeleEtape();
        m.setNom(req.getNom());
        m.setDescription(req.getDescription());
        m.setOrdre(req.getOrdre());
        m.getSpecialites().addAll(new java.util.HashSet<>(specs));
        ModeleEtape saved = modeleEtapeRepository.save(m);
        java.util.List<Integer> ids = specs.stream().map(Specialite::getId).collect(Collectors.toList());
        java.util.List<String> labels = specs.stream().map(Specialite::getLibelle).collect(Collectors.toList());
        return new ModeleEtapeResponse(saved.getId(), saved.getNom(), saved.getDescription(), saved.getOrdre(), ids, labels);
    }

    public IllustrationResponse addIllustrationToModele(Integer modeleId, IllustrationCreateRequest req, org.springframework.web.multipart.MultipartFile image) {
        ModeleEtape modele = modeleEtapeRepository.findById(modeleId)
                .orElseThrow(() -> new IllegalArgumentException("Modèle d'étape introuvable: " + modeleId));
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image requise");
        }
        try {
            String baseDir = System.getProperty("user.dir");
            Path uploadDir = Path.of(baseDir, "uploads", "illustrations");
            Files.createDirectories(uploadDir);
            String original = image.getOriginalFilename();
            String filename = java.util.UUID.randomUUID() + (original != null ? ("_" + original.replaceAll("[^a-zA-Z0-9._-]", "_")) : "");
            Path target = uploadDir.resolve(filename);
            try (java.io.InputStream in = image.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            Illustration ill = new Illustration();
            ill.setTitre(req.getTitre());
            ill.setDescription(req.getDescription());
            ill.setUrlImage(target.toString().replace('\\', '/'));
            ill.setModele(modele);
            Illustration saved = illustrationRepository.save(ill);
            return new IllustrationResponse(saved.getId(), saved.getTitre(), saved.getDescription(), saved.getUrlImage(), modele.getId());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Echec d'enregistrement de l'image: " + ex.getMessage());
        }
    }
}
