package com.ngaso.Ngaso.dto;

public class DemandeCreateRequest {
    private Integer professionnelId;
    private String message;

    public Integer getProfessionnelId() { return professionnelId; }
    public void setProfessionnelId(Integer professionnelId) { this.professionnelId = professionnelId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
