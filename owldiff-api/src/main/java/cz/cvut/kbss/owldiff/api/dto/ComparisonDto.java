package cz.cvut.kbss.owldiff.api.dto;

public class ComparisonDto {

    private OntologyDataDto original;

    private OntologyDataDto update;

    private String sessionId;

    private String sessionTimer;

    private String guiUrl;

    public OntologyDataDto getOriginal() {
        return original;
    }

    public void setOriginal(OntologyDataDto original) {
        this.original = original;
    }

    public OntologyDataDto getUpdate() {
        return update;
    }

    public void setUpdate(OntologyDataDto update) {
        this.update = update;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionTimer() {
        return sessionTimer;
    }

    public void setSessionTimer(String sessionTimer) {
        this.sessionTimer = sessionTimer;
    }

    public String getGuiUrl() {
        return guiUrl;
    }

    public void setGuiUrl(String guiUrl) {
        this.guiUrl = guiUrl;
    }
}
