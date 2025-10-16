package com.ngaso.Ngaso.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ngaso.Ngaso.Services.ProjetService;
import com.ngaso.Ngaso.dto.ProjetCreateRequest;
import com.ngaso.Ngaso.dto.ProjetResponse;

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

    @GetMapping
    public ResponseEntity<List<ProjetResponse>> listByNovice(@RequestParam Integer noviceId) {
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
}

