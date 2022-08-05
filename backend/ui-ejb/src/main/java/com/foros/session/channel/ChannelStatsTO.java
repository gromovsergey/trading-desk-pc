package com.foros.session.channel;

import java.util.List;

public class ChannelStatsTO {
    // table 1
    private List<ChannelActivityTO> activity;
    private boolean triggersStatPresent;
    // table 2
    private ServingStatsTO serving;
    // overlap
    private List<ChannelOverlapTO> channelOverlap;
    // also used
    private List<ChannelAlsoUsedTO> alsoUsed;

    public List<ChannelActivityTO> getActivity() {
        return activity;
    }

    public void setActivity(List<ChannelActivityTO> activity) {
        this.activity = activity;
    }

    public boolean isTriggersStatPresent() {
        return triggersStatPresent;
    }

    public void setTriggersStatPresent(boolean triggersStatPresent) {
        this.triggersStatPresent = triggersStatPresent;
    }

    public ServingStatsTO getServing() {
        return serving;
    }

    public void setServing(ServingStatsTO serving) {
        this.serving = serving;
    }

    public List<ChannelOverlapTO> getChannelOverlap() {
        return channelOverlap;
    }

    public void setChannelOverlap(List<ChannelOverlapTO> channelOverlap) {
        this.channelOverlap = channelOverlap;
    }

    public List<ChannelAlsoUsedTO> getAlsoUsed() {
        return alsoUsed;
    }

    public void setAlsoUsed(List<ChannelAlsoUsedTO> alsoUsed) {
        this.alsoUsed = alsoUsed;
    }
}
