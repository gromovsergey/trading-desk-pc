package com.foros.model.channel;

import com.foros.annotations.AllowedStatuses;
import com.foros.model.Status;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("S")
@AllowedStatuses(values = {Status.ACTIVE, Status.DELETED})
public class BannedChannel extends TriggersChannel {

    public static final long NO_ADV_CHANNEL_ID = 1;

    public static final long NO_TRACK_CHANNEL_ID = 2;

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.SPECIAL;
    }

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_SPECIAL;
    }
}
