package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Services.AdminService;
import com.ngaso.Ngaso.DAO.SpecialiteRepository;
import com.ngaso.Ngaso.Models.entites.Specialite;
import com.ngaso.Ngaso.dto.SpecialiteCreateRequest;
import com.ngaso.Ngaso.dto.ProfessionnelSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Professionnel> validate(@PathVariable Integer id) {
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
}
