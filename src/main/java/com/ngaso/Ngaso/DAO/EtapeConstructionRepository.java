package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.ngaso.Ngaso.Models.entites.EtapeConstruction;

public interface EtapeConstructionRepository extends JpaRepository<EtapeConstruction, Integer> {
    List<EtapeConstruction> findByProjet_IdProjet(Integer projetId);
}
