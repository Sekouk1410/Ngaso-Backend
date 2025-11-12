package com.ngaso.Ngaso.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ngaso.Ngaso.DAO.ConversationRepository;
import com.ngaso.Ngaso.Models.entites.Conversation;

@Component
public class JwtStompChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtStompChannelInterceptor.class);
    private final JwtService jwtService;
    private final ConversationRepository conversationRepository;
    private static final Pattern CONV_TOPIC_PATTERN = Pattern.compile("^/topic/conversations/([0-9]+)$");

    public JwtStompChannelInterceptor(JwtService jwtService, ConversationRepository conversationRepository) {
        this.jwtService = jwtService;
        this.conversationRepository = conversationRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        StompCommand command = accessor.getCommand();
        if (command == null) return message;

        switch (command) {
            case CONNECT -> authenticateConnect(accessor);
            case SUBSCRIBE -> {
                ensureAuthenticated(accessor);
                authorizeSubscribe(accessor);
            }
            case SEND -> ensureAuthenticated(accessor);
            default -> {}
        }
        return message;
    }

    private void authenticateConnect(StompHeaderAccessor accessor) {
        String authHeader = firstNonNull(
                accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION),
                accessor.getFirstNativeHeader("authorization")
        );
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            String msg = "STOMP CONNECT rejected: missing Authorization Bearer token";
            log.warn("{} sessionId={}", msg, accessor.getSessionId());
            throw new MessagingException(msg);
        }
        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            String msg = "STOMP CONNECT rejected: invalid or expired JWT token";
            log.warn("{} sessionId={}", msg, accessor.getSessionId());
            throw new MessagingException(msg);
        }
        Claims claims = jwtService.parseAllClaims(token);
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                role != null ? List.of(new SimpleGrantedAuthority("ROLE_" + role)) : List.of()
        );
        accessor.setUser(authentication);
        log.debug("STOMP CONNECT authenticated userId={} role={} sessionId={}", userId, role, accessor.getSessionId());
    }

    private void ensureAuthenticated(StompHeaderAccessor accessor) {
        if (accessor.getUser() == null) {
            String msg = "STOMP frame rejected: unauthenticated session (send Authorization header on CONNECT)";
            log.warn("{} sessionId={}", msg, accessor.getSessionId());
            throw new MessagingException(msg);
        }
    }

    private void authorizeSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            String msg = "SUBSCRIBE rejected: missing destination header";
            log.warn("{} sessionId={}", msg, accessor.getSessionId());
            throw new MessagingException(msg);
        }
        Matcher m = CONV_TOPIC_PATTERN.matcher(destination);
        if (!m.matches()) {
            // For other destinations, keep default behavior (already authenticated)
            return;
        }
        String principalName = accessor.getUser() != null ? accessor.getUser().getName() : null;
        if (principalName == null) {
            String msg = "SUBSCRIBE rejected: unauthenticated session";
            log.warn("{} destination={} sessionId={}", msg, destination, accessor.getSessionId());
            throw new MessagingException(msg);
        }
        Integer authUserId;
        try {
            authUserId = Integer.valueOf(principalName);
        } catch (NumberFormatException e) {
            String msg = "SUBSCRIBE rejected: STOMP principal is not a numeric user id";
            log.warn("{} destination={} sessionId={}", msg, destination, accessor.getSessionId());
            throw new MessagingException(msg);
        }
        Integer conversationId = Integer.valueOf(m.group(1));
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> {
                    String msg = "SUBSCRIBE rejected: conversation introuvable id=" + conversationId;
                    log.warn("{} userId={} destination={} sessionId={}", msg, authUserId, destination, accessor.getSessionId());
                    return new MessagingException(msg);
                });
        Integer noviceId = c.getProposition() != null && c.getProposition().getNovice() != null ? c.getProposition().getNovice().getId() : null;
        Integer proId = c.getProposition() != null && c.getProposition().getProfessionnel() != null ? c.getProposition().getProfessionnel().getId() : null;
        if (!(authUserId.equals(noviceId) || authUserId.equals(proId))) {
            String msg = "SUBSCRIBE rejected: user " + authUserId + " is not a participant of conversation " + conversationId;
            log.warn("{} destination={} sessionId={}", msg, destination, accessor.getSessionId());
            throw new MessagingException(msg);
        }
        log.debug("SUBSCRIBE authorized: userId={} conversationId={} sessionId={}", authUserId, conversationId, accessor.getSessionId());
    }

    private static String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }
}
