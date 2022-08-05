package app.programmatic.ui.agentreport.dao.model;

public enum AgentReportStatus {
    OPEN("O"),
    CLOSED("C");

    private String letter;

    public static AgentReportStatus valueOfLetter(String letter) {
        for (AgentReportStatus status : values()) {
            if (status.getLetter().equals(letter)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status " + letter);
    }

    AgentReportStatus(String letter) {
        this.letter = letter;
    }

    public String getLetter() {
        return letter;
    }
}