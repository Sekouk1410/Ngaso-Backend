package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT COUNT(m) FROM Utilisateur u JOIN u.conversations c JOIN c.messages m " +
           "WHERE u.id = :professionnelId AND m.estLu = false AND m.expediteur.id <> :professionnelId")
    long countUnreadForProfessionnel(@Param("professionnelId") Integer professionnelId);
}
