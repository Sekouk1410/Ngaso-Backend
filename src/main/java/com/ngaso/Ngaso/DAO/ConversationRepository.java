package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Conversation;
import java.util.Optional;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    Optional<Conversation> findByProposition_Id(Integer propositionId);

    List<Conversation> findByProposition_Novice_Id(Integer noviceId);

    List<Conversation> findByProposition_Professionnel_Id(Integer professionnelId);
}
