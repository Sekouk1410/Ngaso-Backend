package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {}
