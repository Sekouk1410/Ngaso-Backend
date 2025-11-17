package com.ngaso.Ngaso.dto;

public class AdminDashboardStatsResponse {

    public static class Metric {
        private long value;
        private int changePercent;

        public long getValue() { return value; }
        public void setValue(long value) { this.value = value; }
        public int getChangePercent() { return changePercent; }
        public void setChangePercent(int changePercent) { this.changePercent = changePercent; }
    }

    public static class RateMetric {
        private int value; // 0..100
        private int changePercent;

        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public int getChangePercent() { return changePercent; }
        public void setChangePercent(int changePercent) { this.changePercent = changePercent; }
    }

    private Metric utilisateursTotaux;
    private Metric projetsActifs;
    private Metric projetsCeMois;
    private RateMetric tauxAchevement;

    public Metric getUtilisateursTotaux() { return utilisateursTotaux; }
    public void setUtilisateursTotaux(Metric utilisateursTotaux) { this.utilisateursTotaux = utilisateursTotaux; }

    public Metric getProjetsActifs() { return projetsActifs; }
    public void setProjetsActifs(Metric projetsActifs) { this.projetsActifs = projetsActifs; }

    public Metric getProjetsCeMois() { return projetsCeMois; }
    public void setProjetsCeMois(Metric projetsCeMois) { this.projetsCeMois = projetsCeMois; }

    public RateMetric getTauxAchevement() { return tauxAchevement; }
    public void setTauxAchevement(RateMetric tauxAchevement) { this.tauxAchevement = tauxAchevement; }
}
