package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {}
