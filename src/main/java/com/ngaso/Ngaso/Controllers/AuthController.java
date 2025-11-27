package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.dto.ChangePasswordRequest;
import com.ngaso.Ngaso.Services.AuthService;
import com.ngaso.Ngaso.dto.AuthLoginRequest;
import com.ngaso.Ngaso.dto.AuthLoginResponse;
import com.ngaso.Ngaso.dto.RefreshTokenRequest;
import com.ngaso.Ngaso.dto.NoviceSignupRequest;
import com.ngaso.Ngaso.dto.ProfessionnelSignupRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register/novice", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthLoginResponse> registerNovice(@RequestBody NoviceSignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerNovice(request));
    }

    @PostMapping(value = "/register/professionnel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthLoginResponse> registerProfessionnel(@RequestPart("data") ProfessionnelSignupRequest request,
                                                                   @RequestPart("document") MultipartFile document) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerProfessionnel(request, document));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthLoginResponse> login(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthLoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            System.out.println("Début du changement de mot de passe");
            System.out.println("Requête reçue - Ancien mot de passe: " + request.getOldPassword() + ", Nouveau mot de passe: " + request.getNewPassword());
            
            // Récupérer l'ID de l'utilisateur connecté
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("Erreur: Utilisateur non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non authentifié");
            }
            
            // Le principal est le nom d'utilisateur (ID utilisateur sous forme de chaîne)
            String username = authentication.getName();
            System.out.println("Tentative de changement de mot de passe pour l'utilisateur ID: " + username);
            
            authService.changePassword(Integer.parseInt(username), request);
            System.out.println("Mot de passe modifié avec succès pour l'utilisateur ID: " + username);
            return ResponseEntity.ok("Mot de passe modifié avec succès");
            
        } catch (NumberFormatException e) {
            System.out.println("Erreur: Format d'ID utilisateur invalide: " + e.getMessage());
            return ResponseEntity.badRequest().body("ID utilisateur invalide");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur de validation: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur inattendue lors du changement de mot de passe: ");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors du changement de mot de passe: " + e.getMessage());
        }
    }
}

