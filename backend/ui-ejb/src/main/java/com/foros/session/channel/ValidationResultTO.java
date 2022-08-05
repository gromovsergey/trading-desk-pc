package com.foros.session.channel;

import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.util.Stats;

public class ValidationResultTO {

    private Stats channels = new Stats();

    private long lineWithErrors = 0;

    private String id;

    private AdvertisingChannelType channelType;

    public long getLineWithErrors() {
        return lineWithErrors;
    }

    public void setLineWithErrors(long lineWithErrors) {
        this.lineWithErrors = lineWithErrors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Stats getChannels() {
        return channels;
    }

    public void setChannels(Stats channels) {
        this.channels = channels;
    }

    public AdvertisingChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(AdvertisingChannelType channelType) {
        this.channelType = channelType;
    }

}
