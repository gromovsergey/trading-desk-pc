package app.programmatic.ui.channel.view;

import app.programmatic.ui.common.model.MajorDisplayStatus;

public class DisplayStatusesView {
    private MajorDisplayStatus channelDisplayStatus;
    private MajorDisplayStatus flightDisplayStatus;
    private MajorDisplayStatus lineItemDisplayStatus;

    public DisplayStatusesView() {
    }

    public MajorDisplayStatus getChannelDisplayStatus() {
        return channelDisplayStatus;
    }

    public void setChannelDisplayStatus(MajorDisplayStatus channelDisplayStatus) {
        this.channelDisplayStatus = channelDisplayStatus;
    }

    public MajorDisplayStatus getFlightDisplayStatus() {
        return flightDisplayStatus;
    }

    public void setFlightDisplayStatus(MajorDisplayStatus flightDisplayStatus) {
        this.flightDisplayStatus = flightDisplayStatus;
    }

    public MajorDisplayStatus getLineItemDisplayStatus() {
        return lineItemDisplayStatus;
    }

    public void setLineItemDisplayStatus(MajorDisplayStatus lineItemDisplayStatus) {
        this.lineItemDisplayStatus = lineItemDisplayStatus;
    }
}
