package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Administrateur;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Integer> {
    boolean existsByEmail(String email);
    Optional<Administrateur> findByEmail(String email);
    Optional<Administrateur> findByEmailIgnoreCase(String email);
}
