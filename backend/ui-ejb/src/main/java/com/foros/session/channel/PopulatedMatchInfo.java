package com.foros.session.channel;

import java.util.List;

public class PopulatedMatchInfo {
    private List<PopulatedBehavioralChannelMatchInfo> behavioralChannels;
    private List<PopulatedDiscoverChannelMatchInfo> discoverChannels;
    private boolean isNumberOfChannelsExceeded;
    private boolean isNumberOfDiscoverChannelsExceeded;
    private int countMaxChannels;

    public PopulatedMatchInfo(List<PopulatedBehavioralChannelMatchInfo> behavioralChannels, List<PopulatedDiscoverChannelMatchInfo> discoverChannels, int countMaxChannels,
            boolean isNumberOfChannelsExceeded, boolean isNumberOfDiscoverChannelsExceeded) {
        this.behavioralChannels = behavioralChannels;
        this.discoverChannels = discoverChannels;
        this.isNumberOfChannelsExceeded = isNumberOfChannelsExceeded;
        this.isNumberOfDiscoverChannelsExceeded = isNumberOfDiscoverChannelsExceeded;
        this.countMaxChannels = countMaxChannels;
    }

    public List<PopulatedBehavioralChannelMatchInfo> getChannels() {
        return behavioralChannels;
    }

    public List<PopulatedDiscoverChannelMatchInfo> getDiscoverChannels() {
        return discoverChannels;
    }

    public boolean isNumberOfDiscoverChannelsExceeded() {
        return isNumberOfDiscoverChannelsExceeded;
    }

    public boolean isNumberOfChannelsExceeded() {
        return isNumberOfChannelsExceeded;
    }

    public int getCountMaxChannels() {
        return countMaxChannels;
    }
}
