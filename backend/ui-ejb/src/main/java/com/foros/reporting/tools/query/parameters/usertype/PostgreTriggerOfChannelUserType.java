package com.foros.reporting.tools.query.parameters.usertype;

import com.foros.util.EqualsUtil;
import com.foros.util.HashUtil;
import com.foros.util.SQLUtil;

public class PostgreTriggerOfChannelUserType {
    private Long channel_id;
    private String channelType;
    private String countryCode;
    private Character triggerType;
    private String originalTrigger;
    private String normalizedTrigger;
    private String triggerGroup;
    private Boolean masked;
    private Boolean negative;

    public PostgreTriggerOfChannelUserType(Long channel_id, String channelType,
            String countryCode, Character triggerType, String originalTrigger,
            String normalizedTrigger, String triggerGroup, Boolean masked,
            Boolean negative) {
        this.channel_id = channel_id;
        this.channelType = channelType;
        this.countryCode = countryCode;
        this.triggerType = triggerType;
        this.originalTrigger = originalTrigger;
        this.normalizedTrigger = normalizedTrigger;
        this.triggerGroup = triggerGroup;
        this.masked = masked;
        this.negative = negative;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostgreTriggerOfChannelUserType)) {
            return false;
        }

        PostgreTriggerOfChannelUserType that = (PostgreTriggerOfChannelUserType) o;

        return EqualsUtil.equals(channel_id, that.channel_id, channelType, that.channelType,
                countryCode, that.countryCode, triggerType, that.triggerType,
                originalTrigger, that.originalTrigger, normalizedTrigger, that.normalizedTrigger,
                triggerGroup, that.triggerGroup, masked, that.masked, negative, that.negative);
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(channel_id, channelType, countryCode, triggerType, originalTrigger,
                normalizedTrigger, triggerGroup, masked, negative);
    }

    @Override
    public String toString() {
        return "(" + channel_id + "," + channelType + "," + SQLUtil.escapeStructValue(countryCode) + "," + SQLUtil.escapeStructValue(triggerType) + "," + SQLUtil.escapeStructValue(originalTrigger)
                + "," + SQLUtil.escapeStructValue(normalizedTrigger) + "," + SQLUtil.escapeStructValue(triggerGroup) + "," + (masked == null ? "" : masked) + "," + (negative == null ? "" : negative) + ")";
    }
}
