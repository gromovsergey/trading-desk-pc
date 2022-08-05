package app.programmatic.ui.flight.dao.model;

public enum CampaignAllocationStatus {
    A("Active"),
    E("Ended")
    ;

    private String description;

    CampaignAllocationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
