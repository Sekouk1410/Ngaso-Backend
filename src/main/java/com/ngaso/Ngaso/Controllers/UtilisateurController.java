package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Services.UserProfileService;
import com.ngaso.Ngaso.DAO.UtilisateurRepository;
import com.ngaso.Ngaso.Models.entites.Utilisateur;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UtilisateurController {

    private final UserProfileService userProfileService;
    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UserProfileService userProfileService, UtilisateurRepository utilisateurRepository) {
        this.userProfileService = userProfileService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMyPhoto(@org.springframework.web.bind.annotation.RequestParam("image") MultipartFile image,
                                           jakarta.servlet.http.HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non authentifié");
        }
        try {
            String username = authentication.getName(); // userId as string
            Integer userId = Integer.parseInt(username);
            String storedPath = userProfileService.updatePhoto(userId, image);
            int idx = storedPath.replace('\\','/').indexOf("/uploads/");
            String relative = idx >= 0 ? storedPath.replace('\\','/').substring(idx + "/uploads/".length()) : storedPath;
            String baseUrl = request.getScheme() + "://" + request.getServerName() + 
                    (request.getServerPort() != 80 && request.getServerPort() != 443 ? ":" + request.getServerPort() : "") +
                    request.getContextPath();
            String photoUrl = baseUrl + "/uploads/" + relative;
            java.util.Map<String, String> body = java.util.Map.of("photoUrl", photoUrl);
            return ResponseEntity.ok(body);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID utilisateur invalide");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'upload de l'image: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non authentifié");
        }
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            Utilisateur u = utilisateurRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
            com.ngaso.Ngaso.dto.UserMeResponse dto = new com.ngaso.Ngaso.dto.UserMeResponse();
            dto.setNom(u.getNom());
            dto.setPrenom(u.getPrenom());
            dto.setEmail(u.getEmail());
            dto.setTelephone(u.getTelephone());
            dto.setAdresse(u.getAdresse());
            return ResponseEntity.ok(dto);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID utilisateur invalide");
        }
    }
}
