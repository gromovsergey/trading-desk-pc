package com.foros.session.campaign;

import com.foros.model.DisplayStatus;
import com.foros.model.channel.KeywordTriggerType;

import java.math.BigDecimal;

public class CCGKeywordTO {
    private final Long id;
    private final Long channelId;
    private final KeywordTriggerType triggerType;

    private final BigDecimal cpc;
    private final BigDecimal impressions;
    private final BigDecimal clicks;
    private final BigDecimal ctr;
    private final BigDecimal ecpm;
    private final BigDecimal cost;
    private final BigDecimal averageActualCPC;
    private BigDecimal audience;
    private final DisplayStatus displayStatus;
    private final boolean negative;

    public CCGKeywordTO(Long id, Long channelId, KeywordTriggerType triggerType, BigDecimal cpc, BigDecimal impressions,
                        BigDecimal clicks, BigDecimal ctr, BigDecimal ecpm, BigDecimal cost, BigDecimal averageActualCPC,
                        BigDecimal audience, DisplayStatus displayStatus, boolean negative) {
        this.negative = negative;
        this.id = id;
        this.channelId = channelId;
        this.triggerType = triggerType;
        this.cpc = cpc;
        this.impressions = impressions;
        this.clicks = clicks;
        this.ctr = ctr;
        this.ecpm = ecpm;
        this.cost = cost;
        this.averageActualCPC = averageActualCPC;
        this.audience = audience;
        this.displayStatus = displayStatus;
    }

    public KeywordTriggerType getTriggerType() {
        return triggerType;
    }

    public BigDecimal getAudience() {
        return audience;
    }

    public BigDecimal getAverageActualCPC() {
        return averageActualCPC;
    }

    public Long getChannelId() {
        return channelId;
    }

    public BigDecimal getClicks() {
        return clicks;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getCpc() {
        return cpc;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public BigDecimal getEcpm() {
        return ecpm;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getImpressions() {
        return impressions;
    }

    public boolean isNegative() {
        return negative;
    }
}
