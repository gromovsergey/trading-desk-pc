package com.foros.session.channel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ChannelPerformanceTO implements Serializable {
    private Long lifetimeImps;
    private Long lifetimeClicks;
    private BigDecimal lifetimeRevenue;
    private Date lastUsed;

    public Long getLifetimeImps() {
        return lifetimeImps;
    }

    public void setLifetimeImps(Long lifetimeImps) {
        this.lifetimeImps = lifetimeImps;
    }

    public Long getLifetimeClicks() {
        return lifetimeClicks;
    }

    public void setLifetimeClicks(Long lifetimeClicks) {
        this.lifetimeClicks = lifetimeClicks;
    }

    public BigDecimal getLifetimeRevenue() {
        return lifetimeRevenue;
    }

    public void setLifetimeRevenue(BigDecimal lifetimeRevenue) {
        this.lifetimeRevenue = lifetimeRevenue;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }
}
