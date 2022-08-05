package com.foros.session.channel;

import java.math.BigDecimal;

public class ChannelOverlapTO {
    private Long channelId;
    private String channelName;
    private BigDecimal totalUniques;
    private BigDecimal commonUniques;
    private BigDecimal commonTriggers;
    private BigDecimal overlapLevel;

    public ChannelOverlapTO(Long channelId, String channelName, BigDecimal totalUniques, BigDecimal commonUniques, BigDecimal commonTriggers, BigDecimal overlapLevel) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.totalUniques = totalUniques;
        this.commonUniques = commonUniques;
        this.commonTriggers = commonTriggers;
        this.overlapLevel = overlapLevel;
    }

    public Long getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public BigDecimal getTotalUniques() {
        return totalUniques;
    }

    public BigDecimal getCommonUniques() {
        return commonUniques;
    }

    public BigDecimal getCommonTriggers() {
        return commonTriggers;
    }

    public BigDecimal getOverlapLevel() {
        return overlapLevel;
    }
}
