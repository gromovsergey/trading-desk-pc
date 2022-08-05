package com.foros.session.channel;

import java.math.BigDecimal;

public class ChannelStatsRow {
    private BigDecimal imps;
    private BigDecimal uniques;
    private BigDecimal ecpm;
    private BigDecimal value;

    public BigDecimal getImps() {
        return imps;
    }

    public void setImps(BigDecimal imps) {
        this.imps = imps;
    }

    public BigDecimal getUniques() {
        return uniques;
    }

    public void setUniques(BigDecimal uniques) {
        this.uniques = uniques;
    }

    public BigDecimal getEcpm() {
        return this.ecpm;
    }

    public void setEcpm(BigDecimal ecpm) {
        this.ecpm = ecpm;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
