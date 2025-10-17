package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.ModeleEtape;
import java.util.List;

public interface ModeleEtapeRepository extends JpaRepository<ModeleEtape, Integer> {
    List<ModeleEtape> findAllByOrderByOrdreAsc();
}
