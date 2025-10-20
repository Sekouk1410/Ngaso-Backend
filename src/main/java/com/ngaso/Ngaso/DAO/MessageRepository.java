package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT COUNT(m) FROM Utilisateur u JOIN u.conversations c JOIN c.messages m " +
           "WHERE u.id = :professionnelId AND m.estLu = false AND m.expediteur.id <> :professionnelId")
    long countUnreadForProfessionnel(@Param("professionnelId") Integer professionnelId);

    Page<Message> findByConversation_IdOrderByDateEnvoiDesc(Integer conversationId, Pageable pageable);

    Message findTop1ByConversation_IdOrderByDateEnvoiDesc(Integer conversationId);

    Optional<Message> findByIdAndConversation_Id(Integer id, Integer conversationId);

    List<Message> findByIdInAndConversation_Id(List<Integer> ids, Integer conversationId);

    List<Message> findByConversation_IdAndEstLuFalseAndExpediteur_IdNot(Integer conversationId, Integer expediteurId);
}
