package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Administrateur;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Integer> {
    boolean existsByEmail(String email);
}
