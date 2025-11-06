package com.ngaso.Ngaso.dto;

public class DashboardNoviceResponse {
    private String nom;
    private String prenom;
    private Long unreadNotifications;
    private LastProjectInfo lastProject;

    public static class LastProjectInfo {
        private Integer id;
        private String titre;
        private Integer totalEtapes;
        private Integer etapesValidees;
        private Integer progressPercent; // 0..100
        private String currentEtape;
        private String prochaineEtape;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
        public Integer getTotalEtapes() { return totalEtapes; }
        public void setTotalEtapes(Integer totalEtapes) { this.totalEtapes = totalEtapes; }
        public Integer getEtapesValidees() { return etapesValidees; }
        public void setEtapesValidees(Integer etapesValidees) { this.etapesValidees = etapesValidees; }
        public Integer getProgressPercent() { return progressPercent; }
        public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
        public String getCurrentEtape() { return currentEtape; }
        public void setCurrentEtape(String currentEtape) { this.currentEtape = currentEtape; }
        public String getProchaineEtape() { return prochaineEtape; }
        public void setProchaineEtape(String prochaineEtape) { this.prochaineEtape = prochaineEtape; }
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public Long getUnreadNotifications() { return unreadNotifications; }
    public void setUnreadNotifications(Long unreadNotifications) { this.unreadNotifications = unreadNotifications; }
    public LastProjectInfo getLastProject() { return lastProject; }
    public void setLastProject(LastProjectInfo lastProject) { this.lastProject = lastProject; }
}
