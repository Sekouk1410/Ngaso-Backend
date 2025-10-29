package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.UtilisateurRepository;
import com.ngaso.Ngaso.Models.entites.Utilisateur;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UtilisateurRepository utilisateurRepository;

    public UserProfileService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
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
            String base = System.getProperty("user.dir");
            Path baseDir = Paths.get(base, "uploads", "avatars");
            Files.createDirectories(baseDir);

            String original = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
            String onlyName = Paths.get(original).getFileName().toString();
            String safeName = UUID.randomUUID() + "-" + onlyName;
            Path target = baseDir.resolve(safeName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            String storedPath = target.toString().replace('\\', '/');
            user.setPhotoProfil(storedPath);
            utilisateurRepository.save(user);
            System.out.println("[UserProfileService] Upload réussi - path=" + storedPath);
            return storedPath;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Echec du téléchargement de l'image de profil: " + ex.getMessage());
        }
    }
}
