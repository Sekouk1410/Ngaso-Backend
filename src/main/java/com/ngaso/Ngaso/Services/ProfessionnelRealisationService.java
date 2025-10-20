package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;

import com.ngaso.Ngaso.dto.RealisationItemResponse;

@Service
public class ProfessionnelRealisationService {

    private final ProfessionnelRepository professionnelRepository;
    private final FileStorageService storageService;

    public ProfessionnelRealisationService(ProfessionnelRepository professionnelRepository, FileStorageService storageService) {
        this.professionnelRepository = professionnelRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public List<String> list(Integer professionnelId) {
        Professionnel p = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(p.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        return p.getRealisations().stream()
                .map(this::normalizePublicUrl)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RealisationItemResponse> listItems(Integer professionnelId) {
        Professionnel p = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(p.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        return p.getRealisations().stream()
                .map(url -> new RealisationItemResponse(extractIdFromUrl(url), normalizePublicUrl(url)))
                .collect(Collectors.toList());
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
    public List<String> addUpload(Integer professionnelId, MultipartFile image) {
        try {
            Professionnel p = professionnelRepository.findById(professionnelId)
                    .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
            if (Boolean.FALSE.equals(p.getEstValider())) {
                throw new AccessDeniedException("Compte professionnel non validé");
            }
            String storedPath = storageService.storeRealisation(professionnelId, image);
            List<String> list = p.getRealisations();
            if (!list.contains(storedPath)) {
                list.add(storedPath);
            }
            professionnelRepository.save(p);
            return list;
        } catch (IOException ex) {
            throw new RuntimeException("Échec de l'upload de l'image de réalisation", ex);
        }
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

    @Transactional
    public List<RealisationItemResponse> removeById(Integer professionnelId, String realisationId) {
        if (realisationId == null || realisationId.isBlank()) {
            throw new IllegalArgumentException("Identifiant de la réalisation manquant");
        }
        Professionnel p = professionnelRepository.findById(professionnelId)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable"));
        if (Boolean.FALSE.equals(p.getEstValider())) {
            throw new AccessDeniedException("Compte professionnel non validé");
        }
        List<String> list = p.getRealisations();
        String toRemove = list.stream()
                .filter(url -> realisationId.equals(extractIdFromUrl(url)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Réalisation introuvable: " + realisationId));
        list.remove(toRemove);
        professionnelRepository.save(p);
        return list.stream().map(url -> new RealisationItemResponse(extractIdFromUrl(url), url)).collect(Collectors.toList());
    }

    private String extractIdFromUrl(String url) {
        if (url == null || url.isBlank()) return url;
        int idx = Math.max(url.lastIndexOf('/'), url.lastIndexOf('\\'));
        return idx >= 0 && idx < url.length() - 1 ? url.substring(idx + 1) : url;
    }

    private String normalizePublicUrl(String url) {
        if (url == null || url.isBlank()) return url;
        String u = url.replace('\\', '/');
        if (u.startsWith("/uploads/")) return u;
        if (u.startsWith("uploads/")) return "/" + u;
        if (u.startsWith("/realisations/")) return "/uploads" + u;
        if (u.startsWith("realisations/")) return "/uploads/" + u;
        return u;
    }
}
