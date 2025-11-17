package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Services.AdminService;
import com.ngaso.Ngaso.DAO.SpecialiteRepository;
import com.ngaso.Ngaso.Models.entites.Specialite;
import com.ngaso.Ngaso.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("/professionnels/{id}/reject")
    public ResponseEntity<ProfessionnelSummaryResponse> reject(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.rejectProfessionnel(id));
    }

    @PostMapping("/specialites")
    public ResponseEntity<Specialite> createSpecialite(@RequestBody SpecialiteCreateRequest request) {
        Specialite s = new Specialite();
        s.setLibelle(request.getLibelle());
        return ResponseEntity.ok(specialiteRepository.save(s));
    }

    @GetMapping("/specialites")
    public ResponseEntity<List<SpecialiteResponse>> listSpecialites() {
        List<SpecialiteResponse> result = specialiteRepository.findAll()
                .stream()
                .map(s -> new SpecialiteResponse(
                        s.getId(),
                        s.getLibelle(),
                        s.getProfessionnels() != null ? (long) s.getProfessionnels().size() : 0L
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/specialites/{id}")
    public ResponseEntity<Void> deleteSpecialite(@PathVariable Integer id) {
        Specialite s = specialiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Spécialité introuvable: " + id));
        specialiteRepository.delete(s);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/projets")
    public ResponseEntity<PagedProjetAdminResponse> listProjets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Page<ProjetAdminItemResponse> p = adminService.listAllProjets(page, size);
        PagedProjetAdminResponse body = new PagedProjetAdminResponse(
                p.getContent(),
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages(),
                p.hasNext()
        );
        return ResponseEntity.ok(body);
    }

    // ====== Modèles d'étapes ======
    @PostMapping("/modeles-etapes")
    public ResponseEntity<ModeleEtapeResponse> createModeleEtape(@RequestBody ModeleEtapeCreateRequest request) {
        return ResponseEntity.ok(adminService.createModeleEtape(request));
    }

    @GetMapping("/modeles-etapes")
    public ResponseEntity<List<ModeleEtapeResponse>> listModelesEtapes() {
        return ResponseEntity.ok(adminService.listModeleEtapes());
    }

    @PostMapping(value = "/modeles-etapes/{modeleId}/illustrations", consumes = {"multipart/form-data"})
    public ResponseEntity<IllustrationResponse> addIllustration(
            @PathVariable Integer modeleId,
            @RequestPart("data") IllustrationCreateRequest data,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(adminService.addIllustrationToModele(modeleId, data, image));
    }

    @GetMapping("/modeles-etapes/{modeleId}/illustrations")
    public ResponseEntity<List<IllustrationResponse>> listIllustrations(@PathVariable Integer modeleId) {
        return ResponseEntity.ok(adminService.listIllustrationsForModele(modeleId));
    }

    @DeleteMapping("/modeles-etapes/{id}")
    public ResponseEntity<Void> deleteModeleEtape(@PathVariable Integer id) {
        adminService.deleteModeleEtape(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/illustrations/{id}")
    public ResponseEntity<Void> deleteIllustration(@PathVariable Integer id) {
        adminService.deleteIllustration(id);
        return ResponseEntity.noContent().build();
    }
}
