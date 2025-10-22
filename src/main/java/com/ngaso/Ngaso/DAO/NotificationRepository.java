package com.ngaso.Ngaso.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ngaso.Ngaso.Models.entites.Notification;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByDestinataire_IdOrderByDateDesc(Integer userId);
    long countByDestinataire_IdAndEstVuFalse(Integer userId);
    java.util.Optional<Notification> findByIdAndDestinataire_Id(Integer id, Integer userId);
}
