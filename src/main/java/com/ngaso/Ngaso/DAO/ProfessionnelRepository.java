package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Professionnel;
import java.util.List;

public interface ProfessionnelRepository extends JpaRepository<Professionnel, Integer> {
    List<Professionnel> findByEstValider(Boolean estValider);
    List<Professionnel> findBySpecialite_IdAndEstValiderTrue(Integer specialiteId);
    List<Professionnel> findBySpecialite_IdInAndEstValiderTrue(List<Integer> specialiteIds);
}
