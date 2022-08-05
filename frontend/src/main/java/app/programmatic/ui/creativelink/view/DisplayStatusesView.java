package app.programmatic.ui.creativelink.view;

import app.programmatic.ui.common.model.MajorDisplayStatus;

public class DisplayStatusesView {
    private MajorDisplayStatus creativeLinkDispayStatus;
    private MajorDisplayStatus lineItemDisplayStatus;
    private MajorDisplayStatus flightDisplayStatus;

    public MajorDisplayStatus getCreativeLinkDispayStatus() {
        return creativeLinkDispayStatus;
    }

    public void setCreativeLinkDispayStatus(MajorDisplayStatus creativeLinkDispayStatus) {
        this.creativeLinkDispayStatus = creativeLinkDispayStatus;
    }

    public MajorDisplayStatus getLineItemDisplayStatus() {
        return lineItemDisplayStatus;
    }

    public void setLineItemDisplayStatus(MajorDisplayStatus lineItemDisplayStatus) {
        this.lineItemDisplayStatus = lineItemDisplayStatus;
    }

    public MajorDisplayStatus getFlightDisplayStatus() {
        return flightDisplayStatus;
    }

    public void setFlightDisplayStatus(MajorDisplayStatus flightDisplayStatus) {
        this.flightDisplayStatus = flightDisplayStatus;
    }
}
