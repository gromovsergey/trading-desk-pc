package app.programmatic.ui.campaign.dao.model;

public class CampaignFlightPart {
    private CampaignDisplayStatus displayStatus;

    public CampaignFlightPart(CampaignDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public CampaignDisplayStatus getDisplayStatus() {
        return displayStatus;
    }
}
