package app.programmatic.ui.channel.view;

import java.util.Collections;
import java.util.List;

public class FlightAdvertisingChannelsView {
    private List<Long> channelIds = Collections.emptyList();
    private Boolean linkSpecialChannelFlag = Boolean.FALSE;

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public Boolean getLinkSpecialChannelFlag() {
        return linkSpecialChannelFlag;
    }

    public void setLinkSpecialChannelFlag(Boolean linkSpecialChannelFlag) {
        this.linkSpecialChannelFlag = linkSpecialChannelFlag;
    }
}
