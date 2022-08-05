package com.foros.model.channel;

import com.foros.annotations.AllowedStatuses;
import com.foros.model.Status;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@DiscriminatorValue("A")
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING_INACTIVATION})
public class AudienceChannel extends Channel {

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_AUDIENCE;
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.ADVERTISING;
    }
}
