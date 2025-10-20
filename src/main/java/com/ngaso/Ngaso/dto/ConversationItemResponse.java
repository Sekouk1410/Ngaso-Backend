package com.ngaso.Ngaso.dto;

import java.util.Date;

public class ConversationItemResponse {
    private Integer id;
    private Integer propositionId;
    private Integer noviceId;
    private Integer professionnelId;
    private String lastMessage;
    private Date lastMessageAt;
    private Boolean active;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPropositionId() { return propositionId; }
    public void setPropositionId(Integer propositionId) { this.propositionId = propositionId; }

    public Integer getNoviceId() { return noviceId; }
    public void setNoviceId(Integer noviceId) { this.noviceId = noviceId; }

    public Integer getProfessionnelId() { return professionnelId; }
    public void setProfessionnelId(Integer professionnelId) { this.professionnelId = professionnelId; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Date getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(Date lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
