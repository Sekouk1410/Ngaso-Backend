package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {}
