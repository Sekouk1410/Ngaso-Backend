package com.ngaso.Ngaso.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ngaso.Ngaso.Services.ProjetService;
import com.ngaso.Ngaso.dto.ProjetCreateRequest;
import com.ngaso.Ngaso.dto.ProjetResponse;
import com.ngaso.Ngaso.dto.EtapeWithIllustrationsResponse;
import com.ngaso.Ngaso.dto.ProjetUpdateRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ngaso.Ngaso.dto.ProfessionnelBriefResponse;
import com.ngaso.Ngaso.dto.DemandeCreateRequest;
import com.ngaso.Ngaso.dto.DemandeBriefResponse;
import com.ngaso.Ngaso.dto.DemandeProjectItemResponse;

import java.util.List;

@RestController
@RequestMapping("/projets")
public class ProjetController {

    @Autowired
    private ProjetService projetService;

    @PostMapping
    public ResponseEntity<ProjetResponse> create(@RequestBody ProjetCreateRequest request) {
        return ResponseEntity.ok(projetService.createProjet(request));
    }

    @PostMapping("/novices/{noviceId}")
    public ResponseEntity<ProjetResponse> createForNovice(@PathVariable Integer noviceId,
                                                          @RequestBody ProjetCreateRequest request) {
        return ResponseEntity.ok(projetService.createProjetForNovice(noviceId, request));
    }

    @GetMapping("/novices/{noviceId}")
    public ResponseEntity<List<ProjetResponse>> listForNovice(@PathVariable Integer noviceId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        if (!authUserId.equals(noviceId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: accès restreint à vos projets");
        }
        return ResponseEntity.ok(projetService.listByNovice(noviceId));
    }

    @GetMapping
    public ResponseEntity<List<ProjetResponse>> listByNovice(@RequestParam Integer noviceId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        if (!authUserId.equals(noviceId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: accès restreint à vos projets");
        }
        return ResponseEntity.ok(projetService.listByNovice(noviceId));
    }

    @GetMapping("/en-cours")
    public ResponseEntity<List<ProjetResponse>> listEnCours() {
        return ResponseEntity.ok(projetService.listEnCours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetResponse> get(@PathVariable Integer id) {
        return ResponseEntity.ok(projetService.getProjet(id));
    }

    @GetMapping("/{id}/etapes")
    public ResponseEntity<List<EtapeWithIllustrationsResponse>> listEtapes(@PathVariable Integer id) {
        return ResponseEntity.ok(projetService.listEtapesWithIllustrations(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetResponse> update(@PathVariable Integer id, @RequestBody ProjetUpdateRequest request) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(projetService.updateProjetByOwner(authUserId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        projetService.deleteProjetByOwner(authUserId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/etapes/{etapeId}/valider")
    public ResponseEntity<EtapeWithIllustrationsResponse> validerEtape(@PathVariable Integer etapeId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(projetService.validateEtapeByOwner(authUserId, etapeId));
    }

    @GetMapping("/etapes/{etapeId}/professionnels")
    public ResponseEntity<List<ProfessionnelBriefResponse>> listProfessionnelsForEtape(@PathVariable Integer etapeId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(projetService.listProfessionnelsForEtapeOwned(authUserId, etapeId));
    }

    @PostMapping("/etapes/{etapeId}/demandes")
    public ResponseEntity<Integer> createDemandeForEtape(@PathVariable Integer etapeId, @RequestBody DemandeCreateRequest request) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        Integer id = projetService.createDemandeForEtapeOwned(authUserId, etapeId, request);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/etapes/{etapeId}/demandes")
    public ResponseEntity<List<DemandeBriefResponse>> listDemandesForEtape(@PathVariable Integer etapeId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(projetService.listDemandesForEtapeOwned(authUserId, etapeId));
    }

    @GetMapping("/{id}/demandes")
    public ResponseEntity<List<DemandeProjectItemResponse>> listDemandesForProjet(@PathVariable Integer id) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(projetService.listDemandesForProjetOwned(authUserId, id));
    }

    @PostMapping("/demandes/{demandeId}/annuler")
    public ResponseEntity<Void> cancelDemande(@PathVariable Integer demandeId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        projetService.cancelDemandeOwned(authUserId, demandeId);
        return ResponseEntity.noContent().build();
    }
}

