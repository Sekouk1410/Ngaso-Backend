package com.ngaso.Ngaso.dto;

import java.util.List;

public class ProfessionnelDashboardResponse {
    private String prenom;
    private String messageBienvenue;

    private long propositionsEnAttente;
    private long propositionsValidees;

    private long demandesTotal;
    private long messagesNonLus;

    private List<String> realisations;

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getMessageBienvenue() { return messageBienvenue; }
    public void setMessageBienvenue(String messageBienvenue) { this.messageBienvenue = messageBienvenue; }

    public long getPropositionsEnAttente() { return propositionsEnAttente; }
    public void setPropositionsEnAttente(long propositionsEnAttente) { this.propositionsEnAttente = propositionsEnAttente; }

    public long getPropositionsValidees() { return propositionsValidees; }
    public void setPropositionsValidees(long propositionsValidees) { this.propositionsValidees = propositionsValidees; }

    public long getDemandesTotal() { return demandesTotal; }
    public void setDemandesTotal(long demandesTotal) { this.demandesTotal = demandesTotal; }

    public long getMessagesNonLus() { return messagesNonLus; }
    public void setMessagesNonLus(long messagesNonLus) { this.messagesNonLus = messagesNonLus; }

    public List<String> getRealisations() { return realisations; }
    public void setRealisations(List<String> realisations) { this.realisations = realisations; }
}
