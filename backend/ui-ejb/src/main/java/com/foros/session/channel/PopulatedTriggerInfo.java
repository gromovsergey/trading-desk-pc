package com.foros.session.channel;

import com.foros.model.channel.trigger.TriggerType;

import java.io.Serializable;

public class PopulatedTriggerInfo implements Serializable {
    private Long channelTriggerId;
    private String trigger;
    private TriggerType triggerType;

    public PopulatedTriggerInfo(Long channelTriggerId, String trigger, char triggerType) {
        this.channelTriggerId = channelTriggerId;
        this.trigger = trigger;
        this.triggerType = TriggerType.byCode(triggerType);
    }

    public String getTrigger() {
        return trigger;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public Long getChannelTriggerId() {
        return channelTriggerId;
    }
}
