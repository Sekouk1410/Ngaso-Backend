package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.ProfessionnelRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private final ProfessionnelRepository professionnelRepository;

    public AdminService(ProfessionnelRepository professionnelRepository) {
        this.professionnelRepository = professionnelRepository;
    }

    @Transactional(readOnly = true)
    public List<Professionnel> listPendingProfessionnels() {
        return professionnelRepository.findByEstValider(false);
    }

    public Professionnel validateProfessionnel(Integer id) {
        Professionnel p = professionnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Professionnel introuvable: " + id));
        p.setEstValider(true);
        return professionnelRepository.save(p); 
    }
}
