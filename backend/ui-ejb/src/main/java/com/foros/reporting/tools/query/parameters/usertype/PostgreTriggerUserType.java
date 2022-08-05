package com.foros.reporting.tools.query.parameters.usertype;

import com.foros.util.SQLUtil;

public class PostgreTriggerUserType {
    private char triggerType;
    private String trigger;
    private String channelType;
    private String countryCode;

    public PostgreTriggerUserType(char triggerType, String trigger, String channelType, String countryCode) {
        this.triggerType = triggerType;
        this.trigger = trigger;
        this.channelType = channelType;
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return "(" + triggerType + "," + SQLUtil.escapeStructValue(trigger) + "," + channelType + "," + SQLUtil.escapeStructValue(countryCode) + ")";
    }
}
