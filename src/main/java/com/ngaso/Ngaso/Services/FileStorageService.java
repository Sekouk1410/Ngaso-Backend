package com.ngaso.Ngaso.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${app.upload.root}") String uploadRoot) throws IOException {
        this.root = Paths.get(uploadRoot).toAbsolutePath().normalize();
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
    }

    public String storeDevis(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier devis manquant");
        }
        Path dir = root.resolve("devis");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String ext = extractExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + (ext.isBlank() ? "" : ("." + ext));
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return buildPublicUrl(target);
    }

    public String storeConversationAttachment(Integer conversationId, MultipartFile file) throws IOException {
        if (conversationId == null) {
            throw new IllegalArgumentException("Identifiant de conversation manquant");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier de message manquant");
        }
        Path dir = root.resolve(Paths.get("conversations", String.valueOf(conversationId)));
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String ext = extractExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + (ext.isBlank() ? "" : ("." + ext));
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return buildPublicUrl(target);
    }

    public String storeRealisation(Integer professionnelId, MultipartFile file) throws IOException {
        if (professionnelId == null) {
            throw new IllegalArgumentException("Identifiant professionnel manquant");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image de réalisation manquante");
        }
        Path dir = root.resolve(Paths.get("realisations", String.valueOf(professionnelId)));
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String ext = extractExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + (ext.isBlank() ? "" : ("." + ext));
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return buildPublicUrl(target);
    }

    private String extractExtension(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return (i >= 0 && i < name.length() - 1) ? name.substring(i + 1) : "";
    }

    private String buildPublicUrl(Path target) {
        // Build a URL path relative to '/uploads/**' mapping
        Path rel = root.relativize(target.toAbsolutePath().normalize());
        String relStr = rel.toString().replace('\\', '/');
        return "/uploads/" + relStr;
    }

    public void deleteByPublicUrl(String publicUrl) throws IOException {
        if (publicUrl == null || publicUrl.isBlank()) return;
        String prefix = "/uploads/";
        if (!publicUrl.startsWith(prefix)) return;
        String rel = publicUrl.substring(prefix.length());
        Path target = root.resolve(rel).normalize();
        if (target.startsWith(root)) {
            Files.deleteIfExists(target);
        }
    }
}
