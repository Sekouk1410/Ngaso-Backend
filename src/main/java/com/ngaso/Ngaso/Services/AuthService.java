package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.NoviceRepository;
import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.DAO.UtilisateurRepository;
import com.ngaso.Ngaso.DAO.AdministrateurRepository;
import com.ngaso.Ngaso.DAO.SpecialiteRepository;
import com.ngaso.Ngaso.dto.ChangePasswordRequest;
import com.ngaso.Ngaso.Models.entites.Novice;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Models.entites.Utilisateur;
import com.ngaso.Ngaso.Models.entites.Administrateur;
import com.ngaso.Ngaso.Models.enums.Role;
import com.ngaso.Ngaso.dto.AuthLoginRequest;
import com.ngaso.Ngaso.dto.AuthLoginResponse;
import com.ngaso.Ngaso.dto.NoviceSignupRequest;
import com.ngaso.Ngaso.dto.ProfessionnelSignupRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import com.ngaso.Ngaso.security.JwtService;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final NoviceRepository noviceRepository;
    private final ProfessionnelRepository professionnelRepository;
    private final SpecialiteRepository specialiteRepository;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UtilisateurRepository utilisateurRepository,
                       NoviceRepository noviceRepository,
                       ProfessionnelRepository professionnelRepository,
                       AdministrateurRepository administrateurRepository,
                       SpecialiteRepository specialiteRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.noviceRepository = noviceRepository;
        this.professionnelRepository = professionnelRepository;
        this.administrateurRepository = administrateurRepository;
        this.specialiteRepository = specialiteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthLoginResponse registerNovice(NoviceSignupRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }
        Novice n = new Novice();
        n.setNom(request.getNom());
        n.setPrenom(request.getPrenom());
        n.setTelephone(request.getTelephone());
        n.setAdresse(request.getAdresse());
        n.setEmail(request.getEmail());
        n.setPassword(passwordEncoder.encode(request.getPassword()));
        n.setRole(Role.Novice);
        Novice saved = noviceRepository.save(n);
        return new AuthLoginResponse(saved.getId(), saved.getRole(), "Inscription réussie", null);
    }

    public AuthLoginResponse registerProfessionnel(ProfessionnelSignupRequest request, org.springframework.web.multipart.MultipartFile document) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }
        Professionnel p = new Professionnel();
        p.setNom(request.getNom());
        p.setPrenom(request.getPrenom());
        p.setTelephone(request.getTelephone());
        p.setAdresse(request.getAdresse());
        p.setEmail(request.getEmail());
        p.setPassword(passwordEncoder.encode(request.getPassword()));
        p.setRole(Role.Professionnel);
        p.setEntreprise(request.getEntreprise());
        p.setDescription(request.getDescription());
        p.setEstValider(false);
        p.setDateCréation(new Date());
        // Handle justificatif upload
        if (document != null && !document.isEmpty()) {
            try {
                String base = System.getProperty("user.dir");
                java.nio.file.Path baseDir = java.nio.file.Paths.get(base, "uploads", "justificatifs");
                java.nio.file.Files.createDirectories(baseDir);
                String original = document.getOriginalFilename() == null ? "file" : document.getOriginalFilename();
                String onlyName = java.nio.file.Paths.get(original).getFileName().toString();
                String safeName = java.util.UUID.randomUUID() + "-" + onlyName;
                java.nio.file.Path target = baseDir.resolve(safeName);
                try (java.io.InputStream in = document.getInputStream()) {
                    java.nio.file.Files.copy(in, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                p.setDocumentJustificatif(target.toString().replace('\\', '/'));
            } catch (java.lang.IllegalStateException | java.io.IOException ex) {
                throw new IllegalArgumentException("Echec d'enregistrement du document justificatif");
            }
        } else {
            throw new IllegalArgumentException("Document justificatif manquant");
        }

        // Single specialite by id
        if (request.getSpecialiteId() == null) {
            throw new IllegalArgumentException("specialiteId est requis");
        }
        com.ngaso.Ngaso.Models.entites.Specialite spec = specialiteRepository.findById(request.getSpecialiteId())
                .orElseThrow(() -> new IllegalArgumentException("Spécialité introuvable"));
        p.setSpecialite(spec);
        Professionnel saved = professionnelRepository.save(p);
        return new AuthLoginResponse(saved.getId(), saved.getRole(), "Inscription réussie", null);
    }

    @Transactional
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        // Vérifier que les mots de passe correspondent
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("La confirmation du nouveau mot de passe ne correspond pas");
        }

        // Trouver l'utilisateur
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        utilisateurRepository.save(user);
    }

    public AuthLoginResponse login(AuthLoginRequest request) {
        // Admin: login via email + password (normalize input)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = request.getEmail().trim().toLowerCase();
            Administrateur admin = administrateurRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides"));
            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                throw new IllegalArgumentException("Identifiants invalides");
            }
            String token = jwtService.generateToken(admin.getId(), Role.Admin.name());
            return new AuthLoginResponse(admin.getId(), Role.Admin, "Connexion réussie", token);
        }

        // Utilisateurs (Novice/Professionnel): login via telephone + password (normalize input)
        if (request.getTelephone() != null && !request.getTelephone().isBlank()) {
            String phone = request.getTelephone().trim();
            Utilisateur user = utilisateurRepository.findByTelephone(phone)
                    .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides"));
            if (Boolean.FALSE.equals(user.getActif())) {
                throw new AccessDeniedException("Compte désactivé");
            }
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Identifiants invalides");
            }
            if (user instanceof Professionnel p && Boolean.FALSE.equals(p.getEstValider())) {
                throw new AccessDeniedException("Compte professionnel non validé");
            }
            String token = jwtService.generateToken(user.getId(), user.getRole().name());
            return new AuthLoginResponse(user.getId(), user.getRole(), "Connexion réussie", token);
        }

        throw new IllegalArgumentException("Fournissez email (admin) ou telephone (utilisateur)");
    }
}

