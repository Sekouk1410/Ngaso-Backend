package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Services.NotificationService;
import com.ngaso.Ngaso.Models.entites.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> listMy() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        List<Notification> list = notificationService.listMy(authUserId);
        List<NotificationResponse> resp = list.stream().map(NotificationResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me/count")
    public ResponseEntity<java.util.Map<String, Long>> countUnread() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        long count = notificationService.countUnread(authUserId);
        return ResponseEntity.ok(java.util.Collections.singletonMap("unread", count));
    }

    @PostMapping("/me/read")
    public ResponseEntity<Void> markAllRead() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        notificationService.markAllRead(authUserId);
        return ResponseEntity.noContent().build();
    }

    public static class NotificationResponse {
        public Integer id;
        public String type;
        public String contenu;
        public Date date;
        public Boolean estVu;
        public static NotificationResponse from(Notification n) {
            NotificationResponse r = new NotificationResponse();
            r.id = n.getId();
            r.type = n.getType() != null ? n.getType().name() : null;
            r.contenu = n.getContenu();
            r.date = n.getDate();
            r.estVu = n.getEstVu();
            return r;
        }
    }
}
