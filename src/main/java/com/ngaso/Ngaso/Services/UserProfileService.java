package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.UtilisateurRepository;
import com.ngaso.Ngaso.Models.entites.Utilisateur;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserProfileService {

    private final UtilisateurRepository utilisateurRepository;
    private final FileStorageService storageService;

    public UserProfileService(UtilisateurRepository utilisateurRepository, FileStorageService storageService) {
        this.utilisateurRepository = utilisateurRepository;
        this.storageService = storageService;
    }

    @Transactional
    public String updatePhoto(Integer userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier image manquant");
        }

        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        try {
            System.out.println("[UserProfileService] Upload démarré - userId=" + userId + ", originalName=" + file.getOriginalFilename() + ", size=" + file.getSize());
            String publicUrl = storageService.storeAvatar(userId, file);
            user.setPhotoProfil(publicUrl);
            utilisateurRepository.save(user);
            System.out.println("[UserProfileService] Upload réussi - url=" + publicUrl);
            return publicUrl;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Echec du téléchargement de l'image de profil: " + ex.getMessage());
        }
    }
}
