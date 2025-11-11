package com.ngaso.Ngaso.Services;

import com.ngaso.Ngaso.DAO.ConversationRepository;
import com.ngaso.Ngaso.DAO.MessageRepository;
import com.ngaso.Ngaso.DAO.UtilisateurRepository;
import com.ngaso.Ngaso.Models.entites.Message;
import com.ngaso.Ngaso.Models.entites.Conversation;
import com.ngaso.Ngaso.Models.entites.PropositionDevis;
import com.ngaso.Ngaso.Models.entites.Utilisateur;
import com.ngaso.Ngaso.dto.ConversationItemResponse;
import com.ngaso.Ngaso.dto.MessageResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public ConversationService(ConversationRepository conversationRepository,
                               MessageRepository messageRepository,
                               UtilisateurRepository utilisateurRepository,
                               FileStorageService fileStorageService,
                               NotificationService notificationService,
                               SimpMessagingTemplate messagingTemplate) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Conversation openOrCreateForProposition(PropositionDevis proposition) {
        if (proposition == null || proposition.getId() == null) {
            throw new IllegalArgumentException("Proposition invalide");
        }
        return conversationRepository.findByProposition_Id(proposition.getId())
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setProposition(proposition);
                    c.setCreatedAt(new java.util.Date());
                    c.setEtat(Boolean.TRUE); // actif
                    return conversationRepository.save(c);
                });
    }

    @Transactional(readOnly = true)
    public java.util.List<ConversationItemResponse> listMyConversations(Integer authUserId, boolean asNovice) {
        java.util.List<Conversation> list = asNovice
                ? conversationRepository.findByProposition_Novice_Id(authUserId)
                : conversationRepository.findByProposition_Professionnel_Id(authUserId);
        return list.stream().map(this::mapItem).toList();
    }

    @Transactional(readOnly = true)
    public java.util.List<MessageResponse> listMessages(Integer authUserId, Integer conversationId, int page, int size) {
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation introuvable"));
        ensureParticipant(authUserId, c);
        Page<Message> p = messageRepository.findByConversation_IdOrderByDateEnvoiDesc(conversationId, PageRequest.of(page, size));
        return p.getContent().stream().map(this::mapMessage).toList();
    }

    @Transactional
    public MessageResponse sendText(Integer authUserId, Integer conversationId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Le contenu du message est vide");
        }
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation introuvable"));
        ensureParticipant(authUserId, c);
        Utilisateur sender = resolveSenderFromConversation(authUserId, c);
        Message m = new Message();
        m.setConversation(c);
        m.setExpediteur(sender);
        m.setContenu(content);
        m.setAttachmentUrl(null);
        m.setDateEnvoi(new java.util.Date());
        m.setEstLu(Boolean.FALSE);
        Message saved = messageRepository.save(m);
        MessageResponse payload = mapMessage(saved);
        // Real-time publish to topic for this conversation
        safePublishToTopic(c.getId(), payload);
        // Notify the other participant
        notifyOtherParticipant(c, sender, content != null ? content : "Pièce jointe envoyée");
        return payload;
    }

    @Transactional
    public MessageResponse sendWithAttachment(Integer authUserId, Integer conversationId, org.springframework.web.multipart.MultipartFile file, String content) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier manquant");
        }
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation introuvable"));
        ensureParticipant(authUserId, c);
        Utilisateur sender = resolveSenderFromConversation(authUserId, c);
        String url;
        try {
            url = fileStorageService.storeConversationAttachment(conversationId, file);
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("Erreur lors de l'upload du fichier du message");
        }
        Message m = new Message();
        m.setConversation(c);
        m.setExpediteur(sender);
        m.setContenu(content);
        m.setAttachmentUrl(url);
        m.setDateEnvoi(new java.util.Date());
        m.setEstLu(Boolean.FALSE);
        Message saved = messageRepository.save(m);
        MessageResponse payload = mapMessage(saved);
        // Real-time publish to topic for this conversation
        safePublishToTopic(c.getId(), payload);
        // Notify the other participant
        notifyOtherParticipant(c, sender, content != null ? content : "Pièce jointe envoyée");
        return payload;
    }

    private Utilisateur resolveSenderFromConversation(Integer authUserId, Conversation c) {
        if (c.getProposition() == null) {
            throw new IllegalStateException("Conversation sans proposition liée");
        }
        var prop = c.getProposition();
        var novice = prop.getNovice();
        var pro = prop.getProfessionnel();
        if (novice != null && novice.getId() != null && novice.getId().equals(authUserId)) {
            return novice; // Novice étend Utilisateur
        }
        if (pro != null && pro.getId() != null && pro.getId().equals(authUserId)) {
            return pro; // Professionnel étend Utilisateur
        }
        throw new org.springframework.security.access.AccessDeniedException("Non autorisé: vous n'êtes pas participant de cette conversation");
    }

    private void ensureParticipant(Integer authUserId, Conversation c) {
        Integer noviceId = c.getProposition() != null && c.getProposition().getNovice() != null ? c.getProposition().getNovice().getId() : null;
        Integer proId = c.getProposition() != null && c.getProposition().getProfessionnel() != null ? c.getProposition().getProfessionnel().getId() : null;
        if (!authUserId.equals(noviceId) && !authUserId.equals(proId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non autorisé: vous n'êtes pas participant de cette conversation");
        }
    }

    private ConversationItemResponse mapItem(Conversation c) {
        ConversationItemResponse r = new ConversationItemResponse();
        r.setId(c.getId());
        r.setActive(Boolean.TRUE.equals(c.getEtat()));
        if (c.getProposition() != null) {
            r.setPropositionId(c.getProposition().getId());
            r.setNoviceId(c.getProposition().getNovice() != null ? c.getProposition().getNovice().getId() : null);
            r.setNoviceNom(c.getProposition().getNovice() != null ? c.getProposition().getNovice().getNom() : null);
            r.setNovicePrenom(c.getProposition().getNovice() != null ? c.getProposition().getNovice().getPrenom() : null);
            r.setProfessionnelId(c.getProposition().getProfessionnel() != null ? c.getProposition().getProfessionnel().getId() : null);
        }
        Message last = messageRepository.findTop1ByConversation_IdOrderByDateEnvoiDesc(c.getId());
        if (last != null) {
            r.setLastMessage(last.getContenu());
            r.setLastMessageAt(last.getDateEnvoi());
        }
        return r;
    }

    private MessageResponse mapMessage(Message m) {
        MessageResponse r = new MessageResponse();
        r.setId(m.getId());
        r.setConversationId(m.getConversation() != null ? m.getConversation().getId() : null);
        r.setSenderId(m.getExpediteur() != null ? m.getExpediteur().getId() : null);
        r.setContent(m.getContenu());
        r.setAttachmentUrl(m.getAttachmentUrl());
        r.setSentAt(m.getDateEnvoi());
        // Determine sender role and participant id (Novice or Professionnel)
        if (m.getConversation() != null && m.getConversation().getProposition() != null && m.getExpediteur() != null) {
            var prop = m.getConversation().getProposition();
            Integer noviceId = prop.getNovice() != null ? prop.getNovice().getId() : null;
            Integer proId = prop.getProfessionnel() != null ? prop.getProfessionnel().getId() : null;
            Integer senderUserId = m.getExpediteur().getId();
            if (noviceId != null && noviceId.equals(senderUserId)) {
                r.setSenderRole("NOVICE");
                r.setSenderParticipantId(noviceId);
            } else if (proId != null && proId.equals(senderUserId)) {
                r.setSenderRole("PROFESSIONNEL");
                r.setSenderParticipantId(proId);
            }
        }
        return r;
    }

    @Transactional
    public void markAsRead(Integer authUserId, Integer conversationId, List<Integer> messageIds) {
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation introuvable"));
        ensureParticipant(authUserId, c);
        List<Message> targets;
        if (messageIds != null && !messageIds.isEmpty()) {
            targets = messageRepository.findByIdInAndConversation_Id(messageIds, conversationId);
        } else {
            targets = messageRepository.findByConversation_IdAndEstLuFalseAndExpediteur_IdNot(conversationId, authUserId);
        }
        for (Message m : targets) {
            if (!Objects.equals(m.getExpediteur() != null ? m.getExpediteur().getId() : null, authUserId)) {
                m.setEstLu(Boolean.TRUE);
            }
        }
        messageRepository.saveAll(targets);
    }

    @Transactional
    public MessageResponse editMessage(Integer authUserId, Integer conversationId, Integer messageId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Le contenu du message est vide");
        }
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation introuvable"));
        ensureParticipant(authUserId, c);
        Message m = messageRepository.findByIdAndConversation_Id(messageId, conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Message introuvable"));
        Integer senderId = m.getExpediteur() != null ? m.getExpediteur().getId() : null;
        if (!Objects.equals(senderId, authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Vous ne pouvez éditer que vos propres messages");
        }
        m.setContenu(content);
        Message saved = messageRepository.save(m);
        return mapMessage(saved);
    }

    @Transactional
    public void deleteMessage(Integer authUserId, Integer conversationId, Integer messageId) {
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation introuvable"));
        ensureParticipant(authUserId, c);
        Message m = messageRepository.findByIdAndConversation_Id(messageId, conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Message introuvable"));
        Integer senderId = m.getExpediteur() != null ? m.getExpediteur().getId() : null;
        if (!Objects.equals(senderId, authUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Vous ne pouvez supprimer que vos propres messages");
        }
        String toDelete = m.getAttachmentUrl();
        messageRepository.delete(m);
        if (toDelete != null && !toDelete.isBlank()) {
            try { fileStorageService.deleteByPublicUrl(toDelete); } catch (java.io.IOException ignored) {}
        }
    }

    private void notifyOtherParticipant(Conversation c, Utilisateur sender, String preview) {
        if (c == null || c.getProposition() == null || sender == null) return;
        var prop = c.getProposition();
        Utilisateur target = null;
        boolean senderIsNovice = false;
        if (prop.getNovice() != null && Objects.equals(sender.getId(), prop.getNovice().getId())) {
            target = prop.getProfessionnel();
            senderIsNovice = true;
        } else if (prop.getProfessionnel() != null && Objects.equals(sender.getId(), prop.getProfessionnel().getId())) {
            target = prop.getNovice();
        }
        if (target != null) {
            String nom = sender.getNom();
            String prenom = sender.getPrenom();
            String senderLabel = ((nom != null ? nom : "") + (prenom != null ? (nom != null ? " " : "") + prenom : "")).trim();
            String contenu = senderIsNovice && !senderLabel.isEmpty() ? ("Nouveau message de " + senderLabel + ": " + preview) : preview;
            notificationService.notify(target, com.ngaso.Ngaso.Models.enums.TypeNotification.Message, contenu);
        }
    }

    private void safePublishToTopic(Integer conversationId, MessageResponse payload) {
        if (conversationId == null || payload == null) return;
        try {
            messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, payload);
        } catch (Exception ignored) {}
    }
}
