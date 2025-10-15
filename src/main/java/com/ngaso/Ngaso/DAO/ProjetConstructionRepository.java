package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.ngaso.Ngaso.Models.entites.ProjetConstruction;

public interface ProjetConstructionRepository extends JpaRepository<ProjetConstruction, Integer> {
    List<ProjetConstruction> findByProprietaire_Id(Integer proprietaireId);
}
