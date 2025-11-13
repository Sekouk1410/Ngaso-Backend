package com.ngaso.Ngaso.Controllers;

import com.ngaso.Ngaso.Services.ConversationService;
import com.ngaso.Ngaso.dto.ConversationItemResponse;
import com.ngaso.Ngaso.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/me")
    public ResponseEntity<List<ConversationItemResponse>> listMyConversations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) auth.getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        boolean isNovice = hasRole(auth, "ROLE_Novice");
        boolean isPro = hasRole(auth, "ROLE_Professionnel");
        if (!isNovice && !isPro) {
            throw new org.springframework.security.access.AccessDeniedException("Rôle requis");
        }
        return ResponseEntity.ok(conversationService.listMyConversations(authUserId, isNovice));
    }

    @GetMapping("/me/unread/total")
    public ResponseEntity<java.util.Map<String, Long>> unreadTotal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) auth.getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        boolean isNovice = hasRole(auth, "ROLE_Novice");
        boolean isPro = hasRole(auth, "ROLE_Professionnel");
        if (!isNovice && !isPro) {
            throw new org.springframework.security.access.AccessDeniedException("Rôle requis");
        }
        long total = conversationService.unreadTotal(authUserId, isNovice);
        return ResponseEntity.ok(java.util.Map.of("total", total));
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> listMessages(@PathVariable Integer conversationId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) auth.getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(conversationService.listMessages(authUserId, conversationId, page, size));
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<MessageResponse> sendText(@PathVariable Integer conversationId,
                                                    @RequestBody Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) auth.getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        String content = body != null ? body.get("content") : null;
        return ResponseEntity.ok(conversationService.sendText(authUserId, conversationId, content));
    }

    @PostMapping("/{conversationId}/messages/upload")
    public ResponseEntity<MessageResponse> sendWithAttachment(@PathVariable Integer conversationId,
                                                              @RequestPart("file") MultipartFile file,
                                                              @RequestPart(value = "content", required = false) String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) auth.getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        return ResponseEntity.ok(conversationService.sendWithAttachment(authUserId, conversationId, file, content));
    }

    @PostMapping("/{conversationId}/messages/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer conversationId,
                                           @RequestBody(required = false) Map<String, List<Integer>> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) auth.getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        List<Integer> ids = body != null ? body.get("messageIds") : null;
        conversationService.markAsRead(authUserId, conversationId, ids);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{conversationId}/messages/{messageId}")
    public ResponseEntity<MessageResponse> editMessage(@PathVariable Integer conversationId,
                                                       @PathVariable Integer messageId,
                                                       @RequestBody Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) auth.getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        String content = body != null ? body.get("content") : null;
        return ResponseEntity.ok(conversationService.editMessage(authUserId, conversationId, messageId, content));
    }

    @DeleteMapping("/{conversationId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer conversationId,
                                              @PathVariable Integer messageId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) auth.getPrincipal();
        Integer authUserId = Integer.parseInt(principal);
        conversationService.deleteMessage(authUserId, conversationId, messageId);
        return ResponseEntity.noContent().build();
    }

    private boolean hasRole(Authentication auth, String role) {
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (ga.getAuthority().equals(role)) return true;
        }
        return false;
    }
}
