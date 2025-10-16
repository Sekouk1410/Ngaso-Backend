package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Services.ProfessionnelDashboardService;
import com.ngaso.Ngaso.Services.ProfessionnelRealisationService;
import com.ngaso.Ngaso.Services.ProfessionnelPropositionService;
import com.ngaso.Ngaso.dto.ProfessionnelDashboardResponse;
import com.ngaso.Ngaso.dto.AddRealisationRequest;
import com.ngaso.Ngaso.dto.CreatePropositionRequest;
import com.ngaso.Ngaso.dto.PropositionDevisResponse;
import com.ngaso.Ngaso.Models.entites.PropositionDevis;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/professionnels")
public class ProfessionnelController {

    private final ProfessionnelDashboardService dashboardService;
    private final ProfessionnelRealisationService realisationService;
    private final ProfessionnelPropositionService propositionService;

    public ProfessionnelController(ProfessionnelDashboardService dashboardService,
                                   ProfessionnelRealisationService realisationService,
                                   ProfessionnelPropositionService propositionService) {
        this.dashboardService = dashboardService;
        this.realisationService = realisationService;
        this.propositionService = propositionService;
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

