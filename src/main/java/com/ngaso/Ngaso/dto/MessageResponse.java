package com.ngaso.Ngaso.dto;

import java.util.Date;

public class MessageResponse {
    private Integer id;
    private Integer conversationId;
    private Integer senderId;
    private String senderRole; // NOVICE or PROFESSIONNEL
    private Integer senderParticipantId; // id du Novice ou du Professionnel
    private String content;
    private String attachmentUrl;
    private Date sentAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getConversationId() { return conversationId; }
    public void setConversationId(Integer conversationId) { this.conversationId = conversationId; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public Integer getSenderParticipantId() { return senderParticipantId; }
    public void setSenderParticipantId(Integer senderParticipantId) { this.senderParticipantId = senderParticipantId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public Date getSentAt() { return sentAt; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }
}
