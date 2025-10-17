package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Services.AdminService;
import com.ngaso.Ngaso.DAO.SpecialiteRepository;
import com.ngaso.Ngaso.Models.entites.Specialite;
import com.ngaso.Ngaso.dto.SpecialiteCreateRequest;
import com.ngaso.Ngaso.dto.UtilisateurSummaryResponse;
import com.ngaso.Ngaso.dto.ProfessionnelSummaryResponse;
import com.ngaso.Ngaso.dto.ModeleEtapeCreateRequest;
import com.ngaso.Ngaso.dto.ModeleEtapeResponse;
import com.ngaso.Ngaso.dto.IllustrationCreateRequest;
import com.ngaso.Ngaso.dto.IllustrationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final SpecialiteRepository specialiteRepository;

    public AdminController(AdminService adminService, SpecialiteRepository specialiteRepository) {
        this.adminService = adminService;
        this.specialiteRepository = specialiteRepository;
    }

    @GetMapping("/professionnels/pending")
    public ResponseEntity<List<ProfessionnelSummaryResponse>> listPending() {
        return ResponseEntity.ok(adminService.listPendingProfessionnels());
    }

    @PostMapping("/professionnels/{id}/validate")
    public ResponseEntity<ProfessionnelSummaryResponse> validate(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.validateProfessionnel(id));
    }

    @PostMapping("/specialites")
    public ResponseEntity<Specialite> createSpecialite(@RequestBody SpecialiteCreateRequest request) {
        Specialite s = new Specialite();
        s.setLibelle(request.getLibelle());
        return ResponseEntity.ok(specialiteRepository.save(s));
    }

    @GetMapping("/specialites")
    public ResponseEntity<List<Specialite>> listSpecialites() {
        return ResponseEntity.ok(specialiteRepository.findAll());
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<UtilisateurSummaryResponse>> listUtilisateurs() {
        return ResponseEntity.ok(adminService.listAllUsers());
    }

    @PostMapping("/utilisateurs/{id}/disable")
    public ResponseEntity<UtilisateurSummaryResponse> disableUser(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.disableUser(id));
    }

    @PostMapping("/utilisateurs/{id}/enable")
    public ResponseEntity<UtilisateurSummaryResponse> enableUser(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.enableUser(id));
    }

    // ====== Modèles d'étapes ======
    @PostMapping("/modeles-etapes")
    public ResponseEntity<ModeleEtapeResponse> createModeleEtape(@RequestBody ModeleEtapeCreateRequest request) {
        return ResponseEntity.ok(adminService.createModeleEtape(request));
    }

    @PostMapping(value = "/modeles-etapes/{modeleId}/illustrations", consumes = {"multipart/form-data"})
    public ResponseEntity<IllustrationResponse> addIllustration(
            @PathVariable Integer modeleId,
            @RequestPart("data") IllustrationCreateRequest data,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(adminService.addIllustrationToModele(modeleId, data, image));
    }
}
