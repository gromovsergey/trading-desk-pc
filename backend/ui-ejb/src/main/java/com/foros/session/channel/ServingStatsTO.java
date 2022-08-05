package com.foros.session.channel;

import java.util.Date;

public class ServingStatsTO {
    private Date statsDate;
    private ChannelStatsRow opportunitiesToServe;
    private ChannelStatsRow served;
    private ChannelStatsRow forosAdServed;
    private ChannelStatsRow nonForosAdServed;

    public Date getStatsDate() {
        return statsDate;
    }

    public void setStatsDate(Date statsDate) {
        this.statsDate = statsDate;
    }

    public ChannelStatsRow getOpportunitiesToServe() {
        return opportunitiesToServe;
    }

    public void setOpportunitiesToServe(ChannelStatsRow opportunitiesToServe) {
        this.opportunitiesToServe = opportunitiesToServe;
    }

    public ChannelStatsRow getServed() {
        return served;
    }

    public void setServed(ChannelStatsRow served) {
        this.served = served;
    }

    public ChannelStatsRow getForosAdServed() {
        return forosAdServed;
    }

    public void setForosAdServed(ChannelStatsRow forosAdServed) {
        this.forosAdServed = forosAdServed;
    }

    public ChannelStatsRow getNonForosAdServed() {
        return nonForosAdServed;
    }

    public void setNonForosAdServed(ChannelStatsRow nonForosAdServed) {
        this.nonForosAdServed = nonForosAdServed;
    }
}
