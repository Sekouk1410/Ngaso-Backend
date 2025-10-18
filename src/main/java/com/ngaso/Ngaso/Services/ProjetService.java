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
import com.ngaso.Ngaso.dto.ProjetUpdateRequest;

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
    public List<ProjetResponse> listEnCours() {
        return projetRepo.findByEtat(EtatProjet.En_Cours)
                .stream().map(this::map).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjetResponse getProjet(Integer id) {
        Optional<ProjetConstruction> opt = projetRepo.findById(id);
        ProjetConstruction p = opt.orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + id));
        return map(p);
    }

    @Transactional(readOnly = true)
    public ProjetResponse getProjetOwned(Integer authUserId, Integer id) {
        ProjetConstruction p = projetRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + id));
        if (p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: accès restreint à vos projets");
        }
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
                            e.getEstValider(),
                            ill
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EtapeWithIllustrationsResponse> listEtapesWithIllustrationsOwned(Integer authUserId, Integer projetId) {
        ProjetConstruction p = projetRepo.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + projetId));
        if (p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: accès restreint à vos projets");
        }
        return listEtapesWithIllustrations(projetId);
    }

    public EtapeWithIllustrationsResponse validateEtapeByOwner(Integer authUserId, Integer etapeId) {
        EtapeConstruction e = etapeRepo.findById(etapeId)
                .orElseThrow(() -> new IllegalArgumentException("Étape introuvable: " + etapeId));
        ProjetConstruction p = e.getProjet();
        if (p == null || p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: cette étape n'appartient pas à votre projet");
        }
        // Règle: validation séquentielle selon l'ordre du modèle
        ModeleEtape currentModel = e.getModele();
        Integer currentOrder = currentModel != null ? currentModel.getOrdre() : null;
        if (currentOrder != null && p.getEtapes() != null) {
            boolean previousAllValidated = p.getEtapes().stream()
                    .filter(other -> other.getIdEtape() != null && !other.getIdEtape().equals(e.getIdEtape()))
                    .filter(other -> other.getModele() != null && other.getModele().getOrdre() != null)
                    .filter(other -> other.getModele().getOrdre() < currentOrder)
                    .allMatch(other -> Boolean.TRUE.equals(other.getEstValider()));
            if (!previousAllValidated) {
                throw new IllegalStateException("Impossible de valider cette étape avant de valider les étapes précédentes");
            }
        }
        e.setEstValider(true);
        EtapeConstruction saved = etapeRepo.save(e);
        ModeleEtape m = saved.getModele();
        List<IllustrationResponse> ill = m != null && m.getIllustrations() != null
                ? m.getIllustrations().stream()
                    .map(i -> new IllustrationResponse(i.getId(), i.getTitre(), i.getDescription(), i.getUrlImage(), m.getId()))
                    .collect(Collectors.toList())
                : java.util.Collections.emptyList();
        return new EtapeWithIllustrationsResponse(
                saved.getIdEtape(),
                m != null ? m.getId() : null,
                m != null ? m.getNom() : null,
                m != null ? m.getDescription() : null,
                m != null ? m.getOrdre() : null,
                saved.getEstValider(),
                ill
        );
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
        if (p.getEtapes() != null) {
            int total = p.getEtapes().size();
            int valides = (int) p.getEtapes().stream().filter(e -> Boolean.TRUE.equals(e.getEstValider())).count();
            r.setTotalEtapes(total);
            r.setEtapesValidees(valides);
        } else {
            r.setTotalEtapes(0);
            r.setEtapesValidees(0);
        }
        return r;
    }

    // ====== Update/Delete by owner (novice) ======
    public ProjetResponse updateProjetByOwner(Integer authUserId, Integer projetId, ProjetUpdateRequest req) {
        ProjetConstruction p = projetRepo.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + projetId));
        if (p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: vous n'êtes pas le propriétaire du projet");
        }
        if (req.getTitre() != null) p.setTitre(req.getTitre());
        if (req.getDimensionsTerrain() != null) p.setDimensionsTerrain(req.getDimensionsTerrain());
        if (req.getBudget() != null) p.setBudget(req.getBudget());
        if (req.getLocalisation() != null) p.setLocalisation(req.getLocalisation());
        ProjetConstruction saved = projetRepo.save(p);
        return map(saved);
    }

    public void deleteProjetByOwner(Integer authUserId, Integer projetId) {
        ProjetConstruction p = projetRepo.findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable: " + projetId));
        if (p.getProprietaire() == null || p.getProprietaire().getId() == null || !p.getProprietaire().getId().equals(authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: vous n'êtes pas le propriétaire du projet");
        }
        projetRepo.delete(p);
    }
}
