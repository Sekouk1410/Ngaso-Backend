package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.dto.ProfessionnelSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final ProfessionnelRepository professionnelRepository;

    public AdminService(ProfessionnelRepository professionnelRepository) {
        this.professionnelRepository = professionnelRepository;
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

    public Professionnel validateProfessionnel(Integer id) {
        Professionnel p = professionnelRepository.findById(id)
             .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable: " + id));
        p.setEstValider(true);
        return professionnelRepository.save(p); 
    }
}
