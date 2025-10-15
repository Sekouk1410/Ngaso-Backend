package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Models.entites.Professionnel;
import com.ngaso.Ngaso.Services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/professionnels/pending")
    public ResponseEntity<List<Professionnel>> listPending() {
        return ResponseEntity.ok(adminService.listPendingProfessionnels());
    }

    @PostMapping("/professionnels/{id}/validate")
    public ResponseEntity<Professionnel> validate(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.validateProfessionnel(id));
    }
}
