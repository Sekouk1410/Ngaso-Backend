package com.ngaso.Ngaso.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ngaso.Ngaso.DAO.EtapeConstructionRepository;
import com.ngaso.Ngaso.DAO.ModeleEtapeRepository;
import com.ngaso.Ngaso.DAO.NoviceRepository;
import com.ngaso.Ngaso.DAO.ProjetConstructionRepository;
import com.ngaso.Ngaso.Models.entites.Novice;
import com.ngaso.Ngaso.Models.entites.ProjetConstruction;
import com.ngaso.Ngaso.Models.entites.ModeleEtape;
import com.ngaso.Ngaso.Models.entites.EtapeConstruction;

import com.ngaso.Ngaso.Models.enums.EtatProjet;
import com.ngaso.Ngaso.dto.ProjetCreateRequest;
import com.ngaso.Ngaso.dto.ProjetResponse;
import com.ngaso.Ngaso.dto.EtapeWithIllustrationsResponse;
import com.ngaso.Ngaso.dto.IllustrationResponse;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjetService {

    private final ProjetConstructionRepository projetRepo;
    private final NoviceRepository noviceRepo;
    private final ModeleEtapeRepository modeleEtapeRepo;
    private final EtapeConstructionRepository etapeRepo;

    public ProjetService(
            ProjetConstructionRepository projetRepo,
            NoviceRepository noviceRepo,
            ModeleEtapeRepository modeleEtapeRepo,
            EtapeConstructionRepository etapeRepo
    ) {
        this.projetRepo = projetRepo;
        this.noviceRepo = noviceRepo;
        this.modeleEtapeRepo = modeleEtapeRepo;
        this.etapeRepo = etapeRepo;
    }

    public ProjetResponse createProjet(ProjetCreateRequest req) {
        Novice proprietaire = noviceRepo.findById(req.getNoviceId())
                .orElseThrow(() -> new IllegalArgumentException("Novice introuvable: " + req.getNoviceId()));

        ProjetConstruction p = new ProjetConstruction();
        p.setTitre(req.getTitre());
        p.setDimensionsTerrain(req.getDimensionsTerrain());
        p.setBudget(req.getBudget());
        p.setLocalisation(req.getLocalisation());
        p.setEtat(EtatProjet.En_Cours);
        p.setDateCréation(new Date());
        p.setProprietaire(proprietaire);

        ProjetConstruction saved = projetRepo.save(p);

        // Initialiser les étapes à partir des modèles (ordre croissant)
        List<ModeleEtape> modeles = modeleEtapeRepo.findAllByOrderByOrdreAsc();

        for (ModeleEtape m : modeles) {
            EtapeConstruction etape = new EtapeConstruction();
            etape.setProjet(saved);
            etape.setModele(m);
            etape.setEstValider(false);
            EtapeConstruction etapeSaved = etapeRepo.save(etape);
            saved.getEtapes().add(etapeSaved);
        }

        return map(saved);
    }

    public ProjetResponse createProjetForNovice(Integer noviceId, ProjetCreateRequest req) {
        Novice proprietaire = noviceRepo.findById(noviceId)
                .orElseThrow(() -> new IllegalArgumentException("Novice introuvable: " + noviceId));

        ProjetConstruction p = new ProjetConstruction();
        p.setTitre(req.getTitre());
        p.setDimensionsTerrain(req.getDimensionsTerrain());
        p.setBudget(req.getBudget());
        p.setLocalisation(req.getLocalisation());
        p.setEtat(EtatProjet.En_Cours);
        p.setDateCréation(new Date());
        p.setProprietaire(proprietaire);

        ProjetConstruction saved = projetRepo.save(p);

        // Initialiser les étapes à partir des modèles (ordre croissant)
        List<ModeleEtape> modeles = modeleEtapeRepo.findAllByOrderByOrdreAsc();

        for (ModeleEtape m : modeles) {
            EtapeConstruction etape = new EtapeConstruction();
            etape.setProjet(saved);
            etape.setModele(m);
            etape.setEstValider(false);
            etapeRepo.save(etape);
            saved.getEtapes().add(etape);
        }

        return map(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjetResponse> listByNovice(Integer noviceId) {
        return projetRepo.findByProprietaire_Id(noviceId)
                .stream().map(this::map).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjetResponse getProjet(Integer id) {
        Optional<ProjetConstruction> opt = projetRepo.findById(id);
        ProjetConstruction p = opt.orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + id));
        return map(p);
    }

    @Transactional(readOnly = true)
    public List<EtapeWithIllustrationsResponse> listEtapesWithIllustrations(Integer projetId) {
        ProjetConstruction p = projetRepo.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + projetId));
        return p.getEtapes().stream()
                .sorted((e1, e2) -> {
                    Integer o1 = e1.getModele() != null ? e1.getModele().getOrdre() : Integer.MAX_VALUE;
                    Integer o2 = e2.getModele() != null ? e2.getModele().getOrdre() : Integer.MAX_VALUE;
                    return Integer.compare(o1 == null ? Integer.MAX_VALUE : o1, o2 == null ? Integer.MAX_VALUE : o2);
                })
                .map(e -> {
                    ModeleEtape m = e.getModele();
                    List<IllustrationResponse> ill = m != null && m.getIllustrations() != null
                            ? m.getIllustrations().stream()
                                .map(i -> new IllustrationResponse(i.getId(), i.getTitre(), i.getDescription(), i.getUrlImage(), m.getId()))
                                .collect(Collectors.toList())
                            : java.util.Collections.emptyList();
                    return new EtapeWithIllustrationsResponse(
                            e.getIdEtape(),
                            m != null ? m.getId() : null,
                            m != null ? m.getNom() : null,
                            m != null ? m.getDescription() : null,
                            m != null ? m.getOrdre() : null,
                            ill
                    );
                })
                .collect(Collectors.toList());
    }

    private ProjetResponse map(ProjetConstruction p) {
        ProjetResponse r = new ProjetResponse();
        r.setId(p.getIdProjet());
        r.setTitre(p.getTitre());
        r.setBudget(p.getBudget());
        r.setLocalisation(p.getLocalisation());
        r.setDimensionsTerrain(p.getDimensionsTerrain());
        r.setEtat(p.getEtat());
        r.setDateCreation(p.getDateCréation());
        return r;
    }
}
