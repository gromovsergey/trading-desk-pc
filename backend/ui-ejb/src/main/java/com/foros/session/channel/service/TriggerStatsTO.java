package com.foros.session.channel.service;

import java.math.BigDecimal;

public class TriggerStatsTO {
    private String originalTrigger;
    private BigDecimal hits;
    private BigDecimal impressions;
    private BigDecimal clicks;
    private BigDecimal ctr;

    public TriggerStatsTO(String originalTrigger, BigDecimal hits, BigDecimal impressions, BigDecimal clicks, BigDecimal ctr) {
        this.originalTrigger = originalTrigger;
        this.hits = hits;
        this.impressions = impressions;
        this.clicks = clicks;
        this.ctr = ctr;
    }

    public String getOriginalTrigger() {
        return originalTrigger;
    }

    public BigDecimal getHits() {
        return hits;
    }

    public BigDecimal getImpressions() {
        return impressions;
    }

    public BigDecimal getClicks() {
        return clicks;
    }

    public BigDecimal getCtr() {
        return ctr;
    }
}
