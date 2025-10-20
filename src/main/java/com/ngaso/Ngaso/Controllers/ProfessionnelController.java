package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Services.ProfessionnelDashboardService;
import com.ngaso.Ngaso.Services.ProfessionnelRealisationService;
import com.ngaso.Ngaso.Services.ProfessionnelPropositionService;
import com.ngaso.Ngaso.Services.ProfessionnelDemandeWorkflowService;
import com.ngaso.Ngaso.dto.ProfessionnelDashboardResponse;
import com.ngaso.Ngaso.dto.AddRealisationRequest;
import com.ngaso.Ngaso.dto.CreatePropositionRequest;
import com.ngaso.Ngaso.dto.PropositionDevisResponse;
import com.ngaso.Ngaso.dto.DemandeUpdateResponse;
import com.ngaso.Ngaso.dto.DemandeProItemResponse;
import com.ngaso.Ngaso.Models.enums.StatutDemande;
import com.ngaso.Ngaso.dto.RealisationItemResponse;
import com.ngaso.Ngaso.Models.entites.PropositionDevis;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/professionnels")
public class ProfessionnelController {

    private final ProfessionnelDashboardService dashboardService;
    private final ProfessionnelRealisationService realisationService;
    private final ProfessionnelPropositionService propositionService;
    private final ProfessionnelDemandeWorkflowService demandeWorkflowService;

    public ProfessionnelController(ProfessionnelDashboardService dashboardService,
                                   ProfessionnelRealisationService realisationService,
                                   ProfessionnelPropositionService propositionService,
                                   ProfessionnelDemandeWorkflowService demandeWorkflowService) {
        this.dashboardService = dashboardService;
        this.realisationService = realisationService;
        this.propositionService = propositionService;
        this.demandeWorkflowService = demandeWorkflowService;
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<ProfessionnelDashboardResponse> getDashboard(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(dashboardService.getDashboard(id));
    }

    @GetMapping("/{id}/realisations")
    public ResponseEntity<java.util.List<String>> listRealisations(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(realisationService.list(id));
    }

    @PostMapping("/{id}/realisations")
    public ResponseEntity<java.util.List<String>> addRealisation(@PathVariable("id") Integer id,
                                                                 @RequestBody AddRealisationRequest request) {
        return ResponseEntity.ok(realisationService.add(id, request.getUrl()));
    }

    @DeleteMapping("/{id}/realisations")
    public ResponseEntity<java.util.List<String>> removeRealisation(@PathVariable("id") Integer id,
                                                                    @RequestParam("url") String url) {
        return ResponseEntity.ok(realisationService.remove(id, url));
    }

    @PostMapping(value = "/{id}/projets/{projetId}/propositions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropositionDevisResponse> proposerPourProjet(@PathVariable("id") Integer professionnelId,
                                                      @PathVariable("projetId") Integer projetId,
                                                      @RequestPart("data") CreatePropositionRequest request,
                                                      @RequestPart(value = "devis", required = false) MultipartFile devisFile) {
        PropositionDevis saved = propositionService.proposerPourProjet(professionnelId, projetId, request, devisFile);
        return ResponseEntity.ok(map(saved));
    }

    @PostMapping(value = "/{id}/projets/{projetId}/propositions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropositionDevisResponse> proposerPourProjetJson(@PathVariable("id") Integer professionnelId,
                                                          @PathVariable("projetId") Integer projetId,
                                                          @RequestBody CreatePropositionRequest request) {
        PropositionDevis saved = propositionService.proposerPourProjet(professionnelId, projetId, request, null);
        return ResponseEntity.ok(map(saved));
    }

    @PostMapping("/{id}/demandes/{demandeId}/validate")
    public ResponseEntity<DemandeUpdateResponse> validateDemande(@PathVariable("id") Integer professionnelId,
                                                                 @PathVariable("demandeId") Integer demandeId) {
        return ResponseEntity.ok(demandeWorkflowService.valider(professionnelId, demandeId));
    }

    @PostMapping("/{id}/demandes/{demandeId}/refuse")
    public ResponseEntity<DemandeUpdateResponse> refuseDemande(@PathVariable("id") Integer professionnelId,
                                                               @PathVariable("demandeId") Integer demandeId) {
        return ResponseEntity.ok(demandeWorkflowService.refuser(professionnelId, demandeId));
    }

    @GetMapping("/{id}/demandes")
    public ResponseEntity<java.util.List<DemandeProItemResponse>> listDemandes(
            @PathVariable("id") Integer professionnelId,
            @RequestParam(value = "statut", required = false) StatutDemande statut
    ) {
        return ResponseEntity.ok(demandeWorkflowService.listDemandes(professionnelId, statut));
    }

    // ====== Endpoints basés sur l'utilisateur connecté ======

    @GetMapping("/me/dashboard")
    public ResponseEntity<ProfessionnelDashboardResponse> getMyDashboard() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(dashboardService.getDashboard(authUserId));
    }

    @GetMapping("/me/realisations")
    public ResponseEntity<java.util.List<String>> listMyRealisations() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(realisationService.list(authUserId));
    }

    @GetMapping("/me/realisations/items")
    public ResponseEntity<java.util.List<RealisationItemResponse>> listMyRealisationsWithIds() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(realisationService.listItems(authUserId));
    }

    @PostMapping("/me/realisations")
    public ResponseEntity<java.util.List<String>> addMyRealisation(@RequestBody AddRealisationRequest request) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(realisationService.add(authUserId, request.getUrl()));
    }

    @DeleteMapping("/me/realisations")
    public ResponseEntity<java.util.List<String>> removeMyRealisation(@RequestParam("url") String url) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(realisationService.remove(authUserId, url));
    }

    @DeleteMapping("/me/realisations/{realisationId}")
    public ResponseEntity<java.util.List<RealisationItemResponse>> removeMyRealisationById(@PathVariable String realisationId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(realisationService.removeById(authUserId, realisationId));
    }

    @PostMapping(value = "/me/realisations/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<java.util.List<String>> uploadMyRealisation(@RequestPart("image") MultipartFile image) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(realisationService.addUpload(authUserId, image));
    }

    @PostMapping(value = "/me/projets/{projetId}/propositions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropositionDevisResponse> proposerPourProjetMeMultipart(@PathVariable("projetId") Integer projetId,
                                                                                  @RequestPart("data") CreatePropositionRequest request,
                                                                                  @RequestPart(value = "devis", required = false) MultipartFile devisFile) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        PropositionDevis saved = propositionService.proposerPourProjet(authUserId, projetId, request, devisFile);
        return ResponseEntity.ok(map(saved));
    }

    @PostMapping(value = "/me/projets/{projetId}/propositions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropositionDevisResponse> proposerPourProjetMeJson(@PathVariable("projetId") Integer projetId,
                                                                             @RequestBody CreatePropositionRequest request) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        PropositionDevis saved = propositionService.proposerPourProjet(authUserId, projetId, request, null);
        return ResponseEntity.ok(map(saved));
    }

    @PostMapping("/me/demandes/{demandeId}/validate")
    public ResponseEntity<DemandeUpdateResponse> validateMyDemande(@PathVariable("demandeId") Integer demandeId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(demandeWorkflowService.valider(authUserId, demandeId));
    }

    @PostMapping("/me/demandes/{demandeId}/refuse")
    public ResponseEntity<DemandeUpdateResponse> refuseMyDemande(@PathVariable("demandeId") Integer demandeId) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(demandeWorkflowService.refuser(authUserId, demandeId));
    }

    @GetMapping("/me/demandes")
    public ResponseEntity<java.util.List<DemandeProItemResponse>> listMyDemandes(@RequestParam(value = "statut", required = false) StatutDemande statut) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(demandeWorkflowService.listDemandes(authUserId, statut));
    }

    private PropositionDevisResponse map(PropositionDevis d) {
        PropositionDevisResponse r = new PropositionDevisResponse();
        r.setId(d.getId());
        r.setMontant(d.getMontant());
        r.setDescription(d.getDescription());
        r.setFichierDevis(d.getFichierDevis());
        r.setStatut(d.getStatut());
        if (d.getSpecialite() != null) {
            r.setSpecialiteId(d.getSpecialite().getId());
        }
        return r;
    }
}

