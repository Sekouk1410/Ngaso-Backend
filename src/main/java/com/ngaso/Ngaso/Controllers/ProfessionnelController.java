package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Services.ProfessionnelDashboardService;
import com.ngaso.Ngaso.Services.ProfessionnelRealisationService;
import com.ngaso.Ngaso.dto.ProfessionnelDashboardResponse;
import com.ngaso.Ngaso.dto.AddRealisationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professionnels")
public class ProfessionnelController {

    private final ProfessionnelDashboardService dashboardService;
    private final ProfessionnelRealisationService realisationService;

    public ProfessionnelController(ProfessionnelDashboardService dashboardService,
                                   ProfessionnelRealisationService realisationService) {
        this.dashboardService = dashboardService;
        this.realisationService = realisationService;
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
}
