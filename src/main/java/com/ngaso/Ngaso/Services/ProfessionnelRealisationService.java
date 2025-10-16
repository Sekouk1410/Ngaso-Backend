package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Service
public class ProfessionnelRealisationService {

    private final ProfessionnelRepository professionnelRepository;

    public ProfessionnelRealisationService(ProfessionnelRepository professionnelRepository) {
        this.professionnelRepository = professionnelRepository;
    }

    @Transactional(readOnly = true)
    public List<String> list(Integer professionnelId) {
        Professionnel p = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(p.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        return p.getRealisations();
    }

    @Transactional
    public List<String> add(Integer professionnelId, String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL de la réalisation manquante");
        }
        Professionnel p = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(p.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        List<String> list = p.getRealisations();
        if (!list.contains(url)) {
            list.add(url);
        }
        professionnelRepository.save(p);
        return list;
    }

    @Transactional
    public List<String> remove(Integer professionnelId, String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL de la réalisation manquante");
        }
        Professionnel p = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(p.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        p.getRealisations().remove(url);
        professionnelRepository.save(p);
        return p.getRealisations();
    }
}
