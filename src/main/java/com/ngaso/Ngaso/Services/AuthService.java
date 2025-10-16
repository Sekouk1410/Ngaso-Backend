package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.NoviceRepository;
import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.DAO.UtilisateurRepository;
import com.ngaso.Ngaso.DAO.AdministrateurRepository;
import com.ngaso.Ngaso.DAO.SpecialiteRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.access.AccessDeniedException;

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
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UtilisateurRepository utilisateurRepository,
                       NoviceRepository noviceRepository,
                       ProfessionnelRepository professionnelRepository,
                       AdministrateurRepository administrateurRepository) {
                       ProfessionnelRepository professionnelRepository,
                       SpecialiteRepository specialiteRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.noviceRepository = noviceRepository;
        this.professionnelRepository = professionnelRepository;
        this.administrateurRepository = administrateurRepository;
        this.specialiteRepository = specialiteRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
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
        return new AuthLoginResponse(saved.getId(), saved.getRole(), "Inscription réussie");
    }

    public AuthLoginResponse registerProfessionnel(ProfessionnelSignupRequest request) {
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
        p.setDocumentJustificatif(request.getDocument_justificatif());
        if (request.getSpecialiteIds() != null && !request.getSpecialiteIds().isEmpty()) {
            Set<com.ngaso.Ngaso.Models.entites.Specialite> specs = new HashSet<>(
                    specialiteRepository.findAllById(request.getSpecialiteIds())
            );
            p.setSpecialites(specs);
        }
        Professionnel saved = professionnelRepository.save(p);
        return new AuthLoginResponse(saved.getId(), saved.getRole(), "Inscription réussie");
    }

    @Transactional(readOnly = true)
    public AuthLoginResponse login(AuthLoginRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Identifiants invalides");
        }
        if (user instanceof Professionnel p && Boolean.FALSE.equals(p.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        return new AuthLoginResponse(user.getId(), user.getRole(), "Connexion réussie");
    }
}

