package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.NotificationRepository;
import com.ngaso.Ngaso.Models.entites.Notification;
import com.ngaso.Ngaso.Models.entites.Utilisateur;
import com.ngaso.Ngaso.Models.enums.TypeNotification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification notify(Utilisateur destinataire, TypeNotification type, String contenu) {
        if (destinataire == null || destinataire.getId() == null) {
            throw new IllegalArgumentException("Destinataire invalide");
        }
        Notification n = new Notification();
        n.setDestinataire(destinataire);
        n.setType(type);
        n.setContenu(contenu);
        n.setDate(new Date());
        n.setEstVu(Boolean.FALSE);
        return notificationRepository.save(n);
    }

    @Transactional(readOnly = true)
    public List<Notification> listMy(Integer userId) {
        return notificationRepository.findByDestinataire_IdOrderByDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public long countUnread(Integer userId) {
        return notificationRepository.countByDestinataire_IdAndEstVuFalse(userId);
    }

    @Transactional
    public void markAllRead(Integer userId) {
        List<Notification> list = notificationRepository.findByDestinataire_IdOrderByDateDesc(userId);
        for (Notification n : list) {
            n.setEstVu(Boolean.TRUE);
        }
        notificationRepository.saveAll(list);
    }

    @Transactional
    public void markOneRead(Integer userId, Integer notificationId) {
        Notification n = notificationRepository.findByIdAndDestinataire_Id(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification introuvable"));
        n.setEstVu(Boolean.TRUE);
        notificationRepository.save(n);
    }
}
