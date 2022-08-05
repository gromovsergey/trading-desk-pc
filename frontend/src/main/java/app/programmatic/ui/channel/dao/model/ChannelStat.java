package app.programmatic.ui.channel.dao.model;

import java.math.BigDecimal;

public class ChannelStat extends Channel {
    private String channelName;
    private Long imps;
    private Long clicks;
    private BigDecimal ctr;
    private Boolean statusChangeable;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Long getImps() {
        return imps;
    }

    public void setImps(Long imps) {
        this.imps = imps;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public void setCtr(BigDecimal ctr) {
        this.ctr = ctr;
    }

    public Boolean getStatusChangeable() {
        return statusChangeable;
    }

    public void setStatusChangeable(Boolean statusChangeable) {
        this.statusChangeable = statusChangeable;
    }
}
