package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Models.enums.StatutDevis;
import com.ngaso.Ngaso.Services.ProjetService;
import com.ngaso.Ngaso.dto.PropositionDevisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/novices")
public class NoviceController {

    @Autowired
    private ProjetService projetService;

    // ====== Propositions - côté Novice ======
    @GetMapping("/me/propositions")
    public ResponseEntity<java.util.List<PropositionDevisResponse>> listMyPropositions(
            @RequestParam(value = "statut", required = false) StatutDevis statut) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(projetService.listPropositionsOwned(authUserId, statut));
    }

    @PostMapping("/me/propositions/{propositionId}/accepter")
    public ResponseEntity<PropositionDevisResponse> accepterProposition(@PathVariable Integer propositionId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(projetService.accepterPropositionOwned(authUserId, propositionId));
    }

    @PostMapping("/me/propositions/{propositionId}/refuser")
    public ResponseEntity<PropositionDevisResponse> refuserProposition(@PathVariable Integer propositionId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(projetService.refuserPropositionOwned(authUserId, propositionId));
    }

    @GetMapping("/me/dashboard")
    public ResponseEntity<com.ngaso.Ngaso.dto.DashboardNoviceResponse> getMyDashboard() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(projetService.getNoviceDashboard(authUserId));
    }
}
