package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.DAO.UtilisateurRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Models.entites.Utilisateur;
import com.ngaso.Ngaso.dto.ProfessionnelSummaryResponse;
import com.ngaso.Ngaso.dto.UtilisateurSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final ProfessionnelRepository professionnelRepository;
    private final UtilisateurRepository utilisateurRepository;

    public AdminService(ProfessionnelRepository professionnelRepository, UtilisateurRepository utilisateurRepository) {
        this.professionnelRepository = professionnelRepository;
        this.utilisateurRepository = utilisateurRepository;
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
                u.getRole()
        );
    }
}
