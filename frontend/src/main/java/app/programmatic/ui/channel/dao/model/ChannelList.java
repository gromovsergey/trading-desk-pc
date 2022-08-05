package app.programmatic.ui.channel.dao.model;

import java.util.List;

public class ChannelList {
    private List<Channel> channels;
    private boolean truncated;

    public ChannelList(List<Channel> channels, boolean truncated) {
        this.channels = channels;
        this.truncated = truncated;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }
}
