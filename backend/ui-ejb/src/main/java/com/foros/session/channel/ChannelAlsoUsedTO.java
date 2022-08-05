package com.foros.session.channel;

import java.io.Serializable;

public class ChannelAlsoUsedTO implements Serializable {
    private long channelId;
    private String channelName;
    private long count;

    public ChannelAlsoUsedTO() { }

    public ChannelAlsoUsedTO(long channelId, String channelName, long count) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.count = count;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}