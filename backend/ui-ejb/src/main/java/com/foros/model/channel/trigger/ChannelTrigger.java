package com.foros.model.channel.trigger;

import com.foros.model.ApproveStatus;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.model.channel.Channel;

public class ChannelTrigger extends EntityBase implements Identifiable {

    private Long id;
    private Channel channel;
    private Long triggerId;
    private Character triggerType;
    private String originalTrigger;
    private char qaStatus = 'H';
    private boolean negative;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        this.registerChange("channel");
    }

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
        this.registerChange("triggerId");
    }

    public Character getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Character triggerType) {
        this.triggerType = triggerType;
        this.registerChange("triggerType");
    }

    public String getOriginalTrigger() {
        return originalTrigger;
    }

    public void setOriginalTrigger(String originalTrigger) {
        this.originalTrigger = originalTrigger;
        this.registerChange("originalTrigger");
    }

    public ApproveStatus getQaStatus() {
        return ApproveStatus.valueOf(qaStatus);
    }

    public void setQaStatus(ApproveStatus qaStatus) {
        this.qaStatus = qaStatus.getLetter();
        this.registerChange("qaStatus");
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.registerChange("negative");
        this.negative = negative;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChannelTrigger)) {
            return false;
        }

        ChannelTrigger other = (ChannelTrigger) object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.trigger.ChannelTrigger[id=" + getId() + "]";
    }
}
